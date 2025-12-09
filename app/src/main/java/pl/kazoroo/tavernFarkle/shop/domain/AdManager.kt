package pl.kazoroo.tavernFarkle.shop.domain

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

object AdManager {
    private var rewardedAd: RewardedAd? = null
    const val TAG = "AdManager"

    fun loadRewardedAd(context: Context) {
        val adRequest = AdManagerAdRequest.Builder().build()

        RewardedAd.load(
            context,
            "ca-app-pub-2857855049969601/1055979641",
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(TAG, "The ad failed to load - $adError")
                    Toast.makeText(context, "Ad failed to load. Try again", Toast.LENGTH_SHORT).show()
                    
                    rewardedAd = null
                }

                override fun onAdLoaded(ad: RewardedAd) {
                    Log.d(TAG, "The ad has been loaded successfully")
                    rewardedAd = ad

                    rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            Log.d(TAG, "Ad was dismissed.")
                            if (rewardedAd == null) {
                                loadRewardedAd(context)
                            }
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            Log.e(TAG, "Ad failed to show $adError")
                        }
                    }
                }
            }
        )
    }

    fun showAd(
        onRewardEarned: (String) -> Unit,
        context: Context
    ) {
        rewardedAd?.let { ad ->
            ad.show(context as Activity) { rewardItem ->
                val rewardAmount = rewardItem.amount

                onRewardEarned(rewardAmount.toString())
            }

            loadRewardedAd(context)
        } ?: run {
            Log.d(TAG, "The rewarded ad wasn't ready yet.")
            Toast.makeText(context, "The ad was not ready yet. Try again.", Toast.LENGTH_LONG).show()
            loadRewardedAd(context)
        }
    }
}