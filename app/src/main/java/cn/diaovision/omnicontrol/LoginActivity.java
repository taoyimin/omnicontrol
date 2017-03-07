package cn.diaovision.omnicontrol;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import cn.diaovision.omnicontrol.widget.CircleCharView;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.input_name)
    AppCompatEditText input_name;
    @BindView(R.id.input_password)
    AppCompatEditText input_password;

    @BindView(R.id.test)
    CircleCharView cView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);


        input_password.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                switch (event.getAction()) {
                    case KeyEvent.ACTION_DOWN:
                        switch (keyCode) {
                            case KeyEvent.KEYCODE_ENTER:
                                login();
                                break;
                            default:
                                break;
                        }
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    /*login handle*/
    private void login() {
        String name = input_name.getText().toString().trim();
        String password = input_password.getText().toString().trim();

        if (name.length() == 0) {
            input_name.requestFocus();
            return;
        }

        if (password.length() == 0) {
            input_password.requestFocus();
            return;
        }

        //TODO: verify name and password
        startActivity(new Intent(LoginActivity.this, MainControlActivity.class));
        this.finish();
//        overridePendingTransition();
    }
}
