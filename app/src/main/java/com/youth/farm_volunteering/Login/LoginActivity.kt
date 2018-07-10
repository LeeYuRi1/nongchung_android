package com.youth.farm_volunteering.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.asksira.loopingviewpagerdemo.ApplicationController
import com.youth.farm_volunteering.R
import com.youth.farm_volunteering.SignUp.BUNDLE_KEY_EMAIL
import com.youth.farm_volunteering.SignUp.BUNDLE_KEY_PASSWORD
import com.youth.farm_volunteering.SignUp.RESULT_REQUEST_LOGIN
import com.youth.farm_volunteering.SignUp.SignupActivity1
import com.youth.farm_volunteering.data.LoginResponseData
import com.youth.farm_volunteering.data.MyPageResponseData
import com.youth.farm_volunteering.login.LoginToken
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        textview_mypage_registration.setOnClickListener {
            var i = Intent(this, SignupActivity1::class.java)
            startActivityForResult(i, 0)
        }
        button_login_login.setOnClickListener { v ->
            requestLogin(edittext_login_email.text.toString(), edittext_login_password.text.toString())
        }
    }

    private fun requestLogin(email: String, password: String) {
        var loginCall = ApplicationController.instance!!.networkService!!.login(email, password);
        loginCall.enqueue(object : Callback<LoginResponseData> {
            override fun onFailure(call: Call<LoginResponseData>, t: Throwable?) {
                Toast.makeText(this@LoginActivity, "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<LoginResponseData>, response: Response<LoginResponseData>) {
                when (response.code()) {
                    200 -> {
                        var sharedPreference = getSharedPreferences(LoginToken.PREF_KEY, Context.MODE_PRIVATE);
                        var editor = sharedPreference.edit();
                        editor.putString(LoginToken.PREF_KEY, response.body().token)
                        editor.commit()
                        LoginToken.token = response.body().token
                        Toast.makeText(this@LoginActivity, response.body().data!!.get(0).name + "로그인 성공!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    else ->
                        Toast.makeText(this@LoginActivity, "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            RESULT_REQUEST_LOGIN ->
                requestLogin(data!!.getStringExtra(BUNDLE_KEY_EMAIL), data!!.getStringExtra(BUNDLE_KEY_PASSWORD))

        }
    }
}
