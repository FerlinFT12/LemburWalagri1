package id.web.codeplace.lemburwalagri;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class user_login_form extends AppCompatActivity {
    EditText nikEt, tgllahirEt;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login_form);

        nikEt = (EditText) findViewById(R.id.txt_nik);
        tgllahirEt = (EditText) findViewById(R.id.txt_tgl_lahir);

        btnLogin = (Button) findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnLogin(view);

                Intent intent = new Intent(user_login_form.this, user_dashboard.class);
                startActivity(intent);
            }
        });
    }

    public void OnLogin(View view) {
        String nik = nikEt.getText().toString();
        String tgllahir = tgllahirEt.getText().toString();
        String type = "login";

        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        backgroundWorker.execute(type, nik, tgllahir);


    }
}
