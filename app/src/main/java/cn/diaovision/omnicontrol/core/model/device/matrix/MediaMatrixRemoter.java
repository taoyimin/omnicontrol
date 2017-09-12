package cn.diaovision.omnicontrol.core.model.device.matrix;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.diaovision.omnicontrol.MainControlActivity;
import cn.diaovision.omnicontrol.core.message.MatrixMessage;
import cn.diaovision.omnicontrol.core.model.device.endpoint.HiCamera;
import cn.diaovision.omnicontrol.core.model.device.matrix.io.Channel;
import cn.diaovision.omnicontrol.rx.RxMessage;
import cn.diaovision.omnicontrol.rx.RxSubscriber;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static cn.diaovision.omnicontrol.core.message.MatrixMessage.buildMultiSwitchMessage;
import static cn.diaovision.omnicontrol.core.message.MatrixMessage.buildSwitchMessage;

/**
 * 矩阵控制器，这里用remoter，以区分controller
 * Created by liulingfeng on 2017/5/5.
 */

public class MediaMatrixRemoter {
    private MediaMatrix matrix;

    public MediaMatrixRemoter(MediaMatrix matrix) {
        this.matrix = matrix;
    }

    public MediaMatrix getMatrix() {
        return matrix;
    }

    public void setMatrix(MediaMatrix matrix) {
        this.matrix = matrix;
    }

    /*get the matrix's online status using getId message*/
    public int getMatrixStatus(RxSubscriber<RxMessage> subscriber) {
        if (matrix == null || !matrix.isReachable())
            return -1;
        final byte[] bytes = MatrixMessage.buildGetIdMessage().toBytes();
        Flowable.create(new FlowableOnSubscribe<RxMessage>() {
            @Override
            public void subscribe(FlowableEmitter<RxMessage> e) throws Exception {
                byte[] recv = matrix.getController().send(bytes, bytes.length);
                if (recv.length > 0) {
                    //if server response, then it is online
                    e.onNext(new RxMessage(RxMessage.DONE));
                } else {
                    //matrix is offline if error happens
                    e.onError(new IOException());
                }
            }
        }, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
        return 0;
    }

    public int switchPreviewVideo(final int portIn, final int portOut, final RxSubscriber<RxMessage> subscriber) {
        if (matrix == null || !matrix.isReachable())
            return -1;
        final MatrixMessage switchMessage = buildSwitchMessage(matrix.id, portIn, portOut);
        Flowable.just(switchMessage)
                .map(new Function<MatrixMessage, RxMessage>() {
                    @Override
                    public RxMessage apply(MatrixMessage matrixMessage) throws Exception {
                        byte[] recv = matrix.getController().send(matrixMessage.toBytes(), matrixMessage.toBytes().length);
                        if (recv.length > 0) {
                            return new RxMessage(RxMessage.DONE);
                        } else {
                            matrix.setReachable(false);
                            throw new IOException();
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
        return 0;
    }

    public int switchVideo(final int portIn, final int[] portOut, final RxSubscriber<RxMessage> subscriber) {
        if (matrix == null || !matrix.isReachable())
            return -1;
        final List<MatrixMessage> msgList = new ArrayList<>();
        for (int o : portOut) {
            int[] pout = new int[1];
            pout[0] = o;
            msgList.add(MatrixMessage.buildStitchMessage(matrix.id, 1, 1, pout));
        }
        MatrixMessage multiSwitchMsg = buildMultiSwitchMessage(matrix.id, portIn, portOut);
        msgList.add(multiSwitchMsg);
        Flowable.intervalRange(0,msgList.size(),0,500, TimeUnit.MILLISECONDS)
                .map(new Function<Long, RxMessage>() {
                    @Override
                    public RxMessage apply(Long aLong) throws Exception {
                        MatrixMessage msg=msgList.get(aLong.intValue());
                        byte[] recv = matrix.getController().send(msg.toBytes(), msg.toBytes().length);
                        if (recv.length > 0) {
                            return new RxMessage(RxMessage.DONE);
                        } else {
                            matrix.setReachable(false);
                            throw new IOException();
                        }
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        matrix.updateChannel(portIn, portOut, Channel.MOD_NORMAL);
                        MainControlActivity.cfg.setChannelSet(matrix.getVideoChnSet());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
        return 0;
    }

    public int stitchVideo(final int portIn, final int columnCnt, final int rowCnt, final int[] portOut, RxSubscriber<RxMessage> subscriber) {
        if (matrix == null || !matrix.isReachable())
            return -1;
        //WM
        MatrixMessage multiSwithMsg = buildMultiSwitchMessage(matrix.id, portIn, portOut);
        //SP PW
        MatrixMessage stitchMsg = MatrixMessage.buildStitchMessage(matrix.id, columnCnt, rowCnt, portOut);
        final List<MatrixMessage> msgList = new ArrayList<>();
        for (int o : portOut) {
            int[] pout = new int[1];
            pout[0] = o;
            msgList.add(MatrixMessage.buildStitchMessage(matrix.id, 1, 1, pout));
        }
        msgList.add(stitchMsg);
        msgList.add(multiSwithMsg);
        Flowable.intervalRange(0, msgList.size(), 0, 500, TimeUnit.MILLISECONDS)
                .map(new Function<Long, RxMessage>() {
                    @Override
                    public RxMessage apply(Long aLong) throws Exception {
                        MatrixMessage msg = msgList.get(aLong.intValue());
                        byte[] recv = matrix.getController().send(msg.toBytes(), msg.toBytes().length);
                        if (recv.length > 0) {
                            return new RxMessage(RxMessage.DONE);
                        } else {
                            matrix.setReachable(false);
                            throw new IOException();
                        }
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        matrix.updateChannel(portIn, portOut, Channel.MOD_STITCH);
                        MainControlActivity.cfg.setChannelSet(matrix.getVideoChnSet());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
        return 0;
    }

    public int startCameraGo(final int portIdx, final int cmd, final int speed, RxSubscriber<RxMessage> subscriber) {
        if (matrix == null || !matrix.isReachable()) {
            return -1;
        }
        final HiCamera cam = matrix.getCameras().get(portIdx);
        if (cam == null) {
            return -1;
        }

        Flowable.create(new FlowableOnSubscribe<RxMessage>() {
            @Override
            public void subscribe(FlowableEmitter<RxMessage> e) throws Exception {

                final byte[] bytes = MatrixMessage.buildStartCameraGoMessage(matrix.id, cam.getBaudrate(), cam.getProto(), cam.getPortIdx(), cmd, speed).toBytes();
                byte[] recv = matrix.getController().send(bytes, bytes.length);
                e.onNext(new RxMessage(RxMessage.DONE));
                e.onComplete();
/*                if (recv != null && recv.length >= 0) {
                    e.onNext(new RxMessage(RxMessage.DONE));
                    e.onComplete();
                } else {
                    matrix.setReachable(false);
                    e.onError(new IOException());
                }*/
            }
        }, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
        return 0;
    }

    public int stopCameraGo(final int portIdx, RxSubscriber<RxMessage> subscriber) {
        if (matrix == null || !matrix.isReachable()) {
            return -1;
        }
        final HiCamera cam = matrix.getCameras().get(portIdx);
        if (cam == null) {
            return -1;
        }

        Flowable.create(new FlowableOnSubscribe<RxMessage>() {
            @Override
            public void subscribe(FlowableEmitter<RxMessage> e) throws Exception {

                byte[] bytes = MatrixMessage.buildStopCameraGoMessage(matrix.id, cam.getBaudrate(), cam.getProto(), cam.getPortIdx()).toBytes();
                byte[] recv = matrix.getController().send(bytes, bytes.length);
                e.onNext(new RxMessage(RxMessage.DONE));
                e.onComplete();
/*                if (recv != null && recv.length >= 0) {
                    e.onNext(new RxMessage(RxMessage.DONE));
                    e.onComplete();
                } else {
                    matrix.setReachable(false);
                    e.onError(new IOException());
                }*/
            }
        }, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
        return 0;
    }

    public int storeCameraPreset(final int portIdx, final int presetIdx, final String name, final RxSubscriber subscriber) {
        if (matrix == null || !matrix.isReachable()) {
            return -1;
        }
        final HiCamera cam = matrix.getCameras().get(portIdx);
        if (cam == null) {
            return -1;
        }

        if (matrix == null)
            return -1;

        Flowable.create(new FlowableOnSubscribe<RxMessage>() {
            @Override
            public void subscribe(FlowableEmitter<RxMessage> e) throws Exception {
                byte[] bytes = MatrixMessage.buildSetCameraPresetMessgae(matrix.id, cam.getBaudrate(), cam.getProto(), portIdx, presetIdx).toBytes();
                byte[] recv = matrix.getController().send(bytes, bytes.length);
                e.onNext(new RxMessage(RxMessage.DONE));
                e.onComplete();
/*                if (recv.length > 0) {
                    e.onNext(new RxMessage(RxMessage.DONE));
                    e.onComplete();
                } else {
                    matrix.setReachable(false);
                    e.onError(new IOException());
                }*/
            }
        }, BackpressureStrategy.BUFFER)
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        //HiCamera.Preset preset = new HiCamera.Preset(name, presetIdx);
                        //cam.updatePreset(preset);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);

        return 0;
    }

    public int loadCameraPreset(final int portIdx, final int presetIdx, RxSubscriber subscriber) {
        if (matrix == null || !matrix.isReachable()) {
            return -1;
        }
        final HiCamera cam = matrix.getCameras().get(portIdx);
        if (cam == null) {
            return -1;
        }

        Flowable.create(new FlowableOnSubscribe<RxMessage>() {
            @Override
            public void subscribe(FlowableEmitter<RxMessage> e) throws Exception {
                byte[] bytes = MatrixMessage.buildLoadCameraPresetMessgae(matrix.id, cam.getBaudrate(), cam.getProto(), portIdx, presetIdx).toBytes();
                byte[] recv = matrix.getController().send(bytes, bytes.length);
                e.onNext(new RxMessage(RxMessage.DONE));
                e.onComplete();
/*                if (recv.length > 0) {
                    e.onNext(new RxMessage(RxMessage.DONE));
                    e.onComplete();
                } else {
                    matrix.setReachable(false);
                    e.onError(new IOException());
                }*/
            }
        }, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);

        return 0;
    }

    public int removeCameraPreset(final int portIdx, final int presetIdx, RxSubscriber subscriber) {
        if (matrix == null || !matrix.isReachable()) {
            return -1;
        }

        final HiCamera cam = matrix.getCameras().get(portIdx);
        if (cam == null || cam.getPreset(presetIdx) >= 0) {
            return -1;
        }

        Flowable.create(new FlowableOnSubscribe<RxMessage>() {
            @Override
            public void subscribe(FlowableEmitter<RxMessage> e) throws Exception {

                byte[] bytes = MatrixMessage.buildClearCameraPresetMessgae(matrix.id, cam.getBaudrate(), cam.getProto(), portIdx, presetIdx).toBytes();
                byte[] recv = matrix.getController().send(bytes, bytes.length);
                e.onNext(new RxMessage(RxMessage.DONE));
                e.onComplete();
/*                if (recv.length > 0) {
                    e.onNext(new RxMessage(RxMessage.DONE));
                    e.onComplete();
                } else {
                    matrix.setReachable(false);
                    e.onError(new IOException());
                }*/
            }
        }, BackpressureStrategy.BUFFER)
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        //cam.deletePreset(cam.getPreset(presetIdx));
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);

        return 0;
    }

    public int setSubtitle(final int portIdx, final String str, RxSubscriber<RxMessage> subscriber) {
        if (matrix == null || !matrix.isReachable()) {
            return -1;
        }
        Flowable.just(str)
                .map(new Function<String, RxMessage>() {
                    @Override
                    public RxMessage apply(String s) throws Exception {
                        byte[] bytes = MatrixMessage.buildSetSubtitleMessage(matrix.id, portIdx, str).toBytes();
                        byte[] recv = matrix.getController().send(bytes, bytes.length);
                        if (recv.length > 0) {
                            return new RxMessage(RxMessage.DONE);
                        } else {
                            throw new IOException();
                        }
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        //TODO: subtitle setup
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
        return 0;
    }

    public int setSubtitleFormat(final int portIdx, final int sublen, final byte fontSize, final byte fontColor, RxSubscriber<RxMessage> subscriber) {
        if (matrix == null || !matrix.isReachable()) {
            return -1;
        }

        Flowable.create(new FlowableOnSubscribe<RxMessage>() {
            @Override
            public void subscribe(FlowableEmitter<RxMessage> e) throws Exception {
                byte[] bytes = MatrixMessage.buildSetSubtitleFormatMessage(matrix.id, sublen, portIdx, fontSize, fontColor).toBytes();
                byte[] recv = matrix.getController().send(bytes, bytes.length);
                if (recv.length > 0) {
                    e.onNext(new RxMessage(RxMessage.DONE));
                } else {
                    throw new IOException();
                }
            }
        }, BackpressureStrategy.BUFFER)
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        //TODO: subtitle setup
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);

        return 0;
    }
}
