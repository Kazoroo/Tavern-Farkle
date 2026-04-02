package pl.kazoroo.tavernFarkle.shop.domain

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import pl.kazoroo.tavernFarkle.R
import kotlin.random.Random

object AdManager {
    private var rewardedAd: RewardedAd? = null
    private var interstitialAd: InterstitialAd? = null
    private const val TAG = "AdManager"

    fun loadRewardedAd(context: Context) {
        val adRequest = AdRequest.Builder().build()

        RewardedAd.load(
            context,
            "ca-app-pub-2857855049969601/1055979641",
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(TAG, "Rewarded ad failed to load - $adError")
                    
                    rewardedAd = null
                }

                override fun onAdLoaded(ad: RewardedAd) {
                    Log.d(TAG, "Rewarded ad has been loaded successfully")
                    rewardedAd = ad

                    rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            Log.d(TAG, "Rewarded ad was dismissed.")
                            if (rewardedAd == null) {
                                loadRewardedAd(context)
                            }
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            Log.e(TAG, "Rewarded ad failed to show $adError")
                        }
                    }
                }
            }
        )
    }

    fun loadInterstitialAd(context: Context) {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            context,
            "ca-app-pub-2857855049969601/6008986714",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    Log.d(TAG, "Interstitial ad was loaded.")
                    interstitialAd = ad
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(TAG, adError.message)
                    interstitialAd = null
                }
            },
        )
    }

    fun showAd(
        onRewardEarned: (String) -> Unit,
        context: Context
    ) {
        rewardedAd?.let { ad ->
            val activity = context as? Activity ?: run {
                Log.e(TAG, "Context is not an Activity")
                return
            }
            interstitialAd?.show(activity)
            ad.show(activity) { rewardItem ->
                val rewardAmount = rewardItem.amount

                onRewardEarned(rewardAmount.toString())
            }

            loadRewardedAd(context)
        } ?: run {
            Log.d(TAG, "The rewarded ad wasn't ready yet.")
            Toast.makeText(context,
                context.getString(R.string.currently_there_are_no_ads_to_display_please_try_again_later), Toast.LENGTH_LONG).show()
            loadRewardedAd(context)
        }
    }

    fun showInterstitial(context: Context, onFinished: () -> Unit) {
        if (Random.nextDouble() > 0.2) {
            onFinished()
            return
        }

        val ad = interstitialAd ?: run {
            Log.d(TAG, "The interstitial ad wasn't ready yet.")
            loadInterstitialAd(context)
            onFinished()
            return
        }

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "Interstitial ad was dismissed.")
                interstitialAd = null
                loadInterstitialAd(context.applicationContext)
                onFinished()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.d(TAG, "Interstitial ad failed to show.")
                interstitialAd = null
                loadInterstitialAd(context)
                onFinished()
            }
        }

        val activity = context as? Activity ?: run {
            Log.e(TAG, "Context is not an Activity")
            onFinished()
            return
        }

        ad.show(activity)
    }
}
