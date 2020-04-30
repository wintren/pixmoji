package rocks.wintren.pixmoji.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import rocks.wintren.pixmoji.R
import rocks.wintren.pixmoji.databinding.ActivityHomeBinding
import rocks.wintren.pixmoji.home.pages.CreatePageFragment
import rocks.wintren.pixmoji.home.pages.DisplayPageFragment
import rocks.wintren.pixmoji.home.pages.OptionsPageFragment
import rocks.wintren.pixmoji.home.pages.PickPageFragment
import rocks.wintren.pixmoji.utils.PageTransformers


class HomeActivity : AppCompatActivity() {

    private lateinit var viewModel: HomeActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupView()
    }

    private fun setupView() {
        viewModel = ViewModelProvider(this).get(HomeActivityViewModel::class.java)
        val binding: ActivityHomeBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_home)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.run {
            pages.run {
                adapter = PagesAdapter(this@HomeActivity)
                setPageTransformer(PageTransformers.ZoomOutSlide)
            }

            TabLayoutMediator(tabLayout, pages) { tab, position ->
                tab.text = when (position) {
                    0 -> "Pick Image"
                    1 -> "Art options"
                    2 -> "Create Art"
                    3 -> "Display Art"
                    else -> throw RuntimeException("Too far, man. Too far...")
                }
            }.attach()
        }
    }

}


class PagesAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    var myItemCount = 4

    override fun getItemCount(): Int {
        return myItemCount
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PickPageFragment()
            1 -> OptionsPageFragment()
            2 -> CreatePageFragment()
            3 -> DisplayPageFragment()
            else -> throw RuntimeException("No page for index $position")
        }
    }

}
