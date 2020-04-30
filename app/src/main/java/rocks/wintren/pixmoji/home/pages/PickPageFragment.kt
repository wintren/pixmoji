package rocks.wintren.pixmoji.home.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import rocks.wintren.pixmoji.R
import rocks.wintren.pixmoji.databinding.FragmentPagePickBinding
import rocks.wintren.pixmoji.home.HomeActivityViewModel

class PickPageFragment() : Fragment() {

    lateinit var parentViewModel: HomeActivityViewModel
    lateinit var pickPageViewModel: PickPageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentViewModel = ViewModelProvider(this).get(HomeActivityViewModel::class.java)
        pickPageViewModel = ViewModelProvider(this).get(PickPageViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return DataBindingUtil.inflate<FragmentPagePickBinding>(
            inflater,
            R.layout.fragment_page_pick,
            container,
            false
        ).run {
            viewModel = pickPageViewModel
            lifecycleOwner = this@PickPageFragment
            root
        }
    }


}