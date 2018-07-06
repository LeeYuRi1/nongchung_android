package com.youth.farm_volunteering.MyPage

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.youth.farm_volunteering.R
import kotlinx.android.synthetic.main.fragment_mypage.*
import android.content.Intent
import com.youth.farm_volunteering.R.id.imageView
import android.provider.MediaStore
import android.graphics.Bitmap
import android.app.Activity
import android.util.Log
import android.provider.MediaStore.Images
import android.widget.ImageView
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_mypage.view.*
import java.io.FileNotFoundException
import java.io.IOException


class MypageFragment : Fragment() {
    private var REQ_CODE_SELECT_IMAGE = 100

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater!!.inflate(R.layout.fragment_mypage, container, false)
        //내 정보 프레그먼트 밑에 있는 계정, 설정, 지원 전부 다 ImageView로 박은다음에 토글 키가 있는 설정은 RelativeLayout으로 두고 match_parent를 가지는
        //ImageView의 background를 '푸시알림'으로 두고 토글키를 오른쪽 끝에다가 alignRight해주자

        //프로필 사진 변경
        v.mypage_profile_image.setOnClickListener(View.OnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = android.provider.MediaStore.Images.Media.CONTENT_TYPE
            intent.data = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            startActivityForResult(intent, REQ_CODE_SELECT_IMAGE)
        })

        //닉네임 변경
        v.nickname.setOnClickListener(View.OnClickListener {
            var nick = Intent(this.context, NicknameFragment::class.java)
            startActivity(nick)
        })

        return v
    }

     override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQ_CODE_SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(this.activity.contentResolver, data!!.data)
                    mypage_profile_image.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    Log.e("test", e.message)
                }
            }
        }
    }



}