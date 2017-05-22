import time
import threading
import Queue

class WorkerThread(threading.Thread):
    def __init__(self, taskQueue):
        threading.Thread.__init__(self)
        self.taskQueue = taskQueue

    def run(self):
        while True:
            time.sleep(0.05)
            task = self.taskQueue.get()
            result = task.go()
            print result

class ThreadPool(threading.Thread):
    def __init__(self, numWorker, timeout = 5, queueMax = 100):
        threading.Thread.__init__(self)
        self.taskQueue = Queue.Queue(maxsize=queueMax)
        self.workers = []
        for m in range(numWorker):
            self.workers.append(WorkerThread(self.taskQueue))

    def run(self):
        for worker in self.workers:
            worker.start()
        for worker in self.workers:
            worker.join()
        print "threadpool"


    def submit(self, task):
        self.taskQueue.put(task)
