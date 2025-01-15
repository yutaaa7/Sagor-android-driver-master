package com.driver.sagardriverapp.uis

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.driver.sagardriverapp.R
import com.driver.sagardriverapp.uis.login.DriverLoginActivity
import com.driver.sagardriverapp.utils.SharedPref
import com.driver.sagardriverapp.utils.Util
import com.driver.sagardriverapp.utils.Util.launchActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initUis()
    }
    private fun initUis() {
        Handler(Looper.getMainLooper()).postDelayed({
          //  launchActivity(DriverLoginActivity::class.java, removeAllPrevActivities = true)
            if (SharedPref.readBoolean(SharedPref.IS_LOGIN)) {

                if (SharedPref.readString(SharedPref.LANGUAGE_ID).equals("0"))
                {
                    Util.setLocale(this@SplashActivity, "en")

                }
                if (SharedPref.readString(SharedPref.LANGUAGE_ID).equals("1"))
                {
                    Util.setLocale(this@SplashActivity, "ar")
                    // Util.changeLanguageAndCheck(this, "ar", R.string.start_booking)

                }
                val bundle = Bundle()
                bundle.putString("where", "login")
                launchActivity(
                    HomeActivity::class.java,
                    removeAllPrevActivities = true,
                    bundle = bundle
                )
            }
            else{
                launchActivity(DriverLoginActivity::class.java, removeAllPrevActivities = true)

            }
        }, 2000)
    }

}
