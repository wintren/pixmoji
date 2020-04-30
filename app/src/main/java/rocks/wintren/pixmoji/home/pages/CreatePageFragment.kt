package rocks.wintren.pixmoji.home.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import rocks.wintren.pixmoji.R
import rocks.wintren.pixmoji.databinding.FragmentPageCreateBinding
import rocks.wintren.pixmoji.home.HomeActivityViewModel

class CreatePageFragment : Fragment() {

    lateinit var parentViewModel: HomeActivityViewModel
    lateinit var createPageViewModel: CreatePageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentViewModel = ViewModelProvider(this).get(HomeActivityViewModel::class.java)
        createPageViewModel = ViewModelProvider(this).get(CreatePageViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return DataBindingUtil.inflate<FragmentPageCreateBinding>(
            inflater,
            R.layout.fragment_page_create,
            container,
            false
        ).run {
            viewModel = createPageViewModel
            lifecycleOwner = this@CreatePageFragment
            root
        }
    }

}