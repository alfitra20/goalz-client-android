package emse.mobisocial.goalz.ui

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import emse.mobisocial.goalz.R
import org.w3c.dom.Text

class OnboardingSlideAdapter(context: Context):PagerAdapter() {

    var mContext:Context= context

    var slideImage = intArrayOf(
            R.drawable.goal_color,
            R.drawable.resource_color,
            R.drawable.explore_icon

    )

    var slideDescription = arrayOf<String>(
            "this is the app",
            "This is the second slide",
            "the last one"
    )

    var slideHeading = arrayOf<String>(
            "Goalz Tracker",
            "This is the second slide",
            "the last one"
    )



    override fun getCount(): Int {
        return 0
    }

    override fun isViewFromObject(view: View?, `object`: Any?): Boolean {
        return view === `object` as RelativeLayout
    }

    @SuppressLint("ResourceType")
    override fun instantiateItem(container: ViewGroup?, position: Int): Any {
        var layoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var view = layoutInflater.inflate(R.id.onboardingSlideView, container, false)

        var onboardingImage = view.findViewById<ImageView>(R.id.onboarding_image)
        var onboardingText = view.findViewById(R.id.onboarding_text) as TextView
        var onboardingHeading = view.findViewById(R.id.onboarding_heading) as TextView

        onboardingImage.setImageResource(slideImage[position])
        onboardingHeading.text = slideHeading[position]
        onboardingText.text = slideDescription[position]

        container?.addView(view)

        return view
    }

    override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any?) {
        container?.removeView(`object` as RelativeLayout)
    }
}