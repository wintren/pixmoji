package rocks.wintren.pixmoji.home.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import rocks.wintren.pixmoji.R
import rocks.wintren.pixmoji.databinding.FragmentPageOptionsBinding
import rocks.wintren.pixmoji.home.HomeActivityViewModel

class OptionsPageFragment : Fragment() {

    lateinit var parentViewModel: HomeActivityViewModel
    lateinit var optionsPageViewModel: OptionsPageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentViewModel = ViewModelProvider(this).get(HomeActivityViewModel::class.java)
        optionsPageViewModel = ViewModelProvider(this).get(OptionsPageViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return DataBindingUtil.inflate<FragmentPageOptionsBinding>(inflater, R.layout.fragment_page_options, container, false).run {
            viewModel = optionsPageViewModel
            lifecycleOwner = this@OptionsPageFragment
            root
        }
    }

}