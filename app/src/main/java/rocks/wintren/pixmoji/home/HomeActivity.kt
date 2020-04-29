package rocks.wintren.pixmoji.home

import android.graphics.Outline
import android.os.Bundle
import android.view.View
import android.view.ViewOutlineProvider
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.activity_home.*
import rocks.wintren.pixmoji.R
import rocks.wintren.pixmoji.d
import rocks.wintren.pixmoji.databinding.ActivityHomeBinding
import rocks.wintren.pixmoji.home.pages.PageFourFragment
import rocks.wintren.pixmoji.home.pages.PageOneFragment
import rocks.wintren.pixmoji.home.pages.PageThreeFragment
import rocks.wintren.pixmoji.home.pages.PageTwoFragment
import rocks.wintren.pixmoji.utils.DepthPageTransformer
import rocks.wintren.pixmoji.utils.PageTransformers
import kotlin.math.abs


class HomeActivity : AppCompatActivity() {

    private lateinit var viewModel: HomeActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewModel()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(HomeActivityViewModel::class.java)
        val binding: ActivityHomeBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_home)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        navigation.outlineProvider = ViewOutlineProvider.BACKGROUND
        val outline = Outline()
        navigation.outlineProvider.getOutline(navigation, outline)
        d("${outline.canClip()}")
        root.clipToOutline = true
        navigation.clipToOutline = true

//        pages.isUserInputEnabled = false
        val pagesAdapter = PagesAdapter(this)
        pages.adapter = pagesAdapter
        pages.setPageTransformer(PageTransformers.ZoomOutSlide)
    }

}


class PagesAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when (position){
            0 -> PageOneFragment()
            1 -> PageTwoFragment()
            2 -> PageThreeFragment()
            3 -> PageFourFragment()
            else -> throw RuntimeException("No page for index $position")
        }
    }

}
