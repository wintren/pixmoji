package rocks.wintren.pixmoji.home.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import rocks.wintren.pixmoji.R
import rocks.wintren.pixmoji.databinding.FragmentPageDisplayBinding
import rocks.wintren.pixmoji.home.HomeActivityViewModel

class DisplayPageFragment : Fragment() {

    private lateinit var parentViewModel: HomeActivityViewModel
    private lateinit var displayPageViewModel: DisplayPageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentViewModel = ViewModelProvider(this).get(HomeActivityViewModel::class.java)
        displayPageViewModel = ViewModelProvider(this).get(DisplayPageViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return DataBindingUtil.inflate<FragmentPageDisplayBinding>(
            inflater,
            R.layout.fragment_page_display,
            container,
            false
        ).run {
            viewModel = displayPageViewModel
            lifecycleOwner = this@DisplayPageFragment
            root
        }
    }

}