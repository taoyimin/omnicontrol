package cn.diaovision.omnicontrol;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.diaovision.omnicontrol.view.LoginContract;
import cn.diaovision.omnicontrol.view.LoginPresenter;

public class LoginActivity extends BaseActivity implements LoginContract.View, View.OnKeyListener {

    @BindView(R.id.input_name)
    AppCompatEditText input_name;
    @BindView(R.id.input_password)
    AppCompatEditText input_password;
    @BindView(R.id.btn_login)
    AppCompatButton btn_login;


    LoginContract.Presenter presenter = new LoginPresenter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        input_name.setOnKeyListener(this);
        input_password.setOnKeyListener(this);
    }

    @OnClick(R.id.btn_login)
    public void login(){
        presenter.login();
    }

    @Override
    public void showToast(String content) {
        Toast.makeText(this,content,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toMainActivity() {
        startActivity(new Intent(LoginActivity.this, MainControlActivity.class));
        this.finish();
    }

    @Override
    public String getName() {
        return input_name.getText().toString().trim();
    }

    @Override
    public String getPassword() {
        return input_password.getText().toString().trim();
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        switch (event.getAction()) {
            case KeyEvent.ACTION_DOWN:
                switch (keyCode) {
                    case KeyEvent.KEYCODE_ENTER:
                        presenter.login();
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
}
