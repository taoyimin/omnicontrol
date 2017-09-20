package cn.diaovision.omnicontrol;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
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
    @BindView(R.id.checkbox)
    CheckBox checkBox;


    LoginContract.Presenter presenter = new LoginPresenter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        input_name.setOnKeyListener(this);
        input_password.setOnKeyListener(this);
        SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        String name = sharedPreferences.getString("name", "admin");
        String password = sharedPreferences.getString("password", "123456");
        boolean checked = sharedPreferences.getBoolean("checked", true);
        if(checked){
            checkBox.setChecked(true);
            input_name.setText(name);
            input_password.setText(password);
        }else{
            checkBox.setChecked(false);
            input_name.setText(name);
        }
    }

    @OnClick(R.id.btn_login)
    public void login() {
        presenter.login();
    }

    @Override
    public void showToast(String content) {
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
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
    public void saveSharedPreferences(String key, String val) {
        SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,val);
        editor.commit();
    }

    @Override
    public void saveSharedPreferences(String key, boolean val) {
        SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key,val);
        editor.commit();
    }

    @Override
    public boolean getCheckState() {
        return checkBox.isChecked();
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
