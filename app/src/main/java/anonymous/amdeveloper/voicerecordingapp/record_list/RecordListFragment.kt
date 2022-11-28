package anonymous.amdeveloper.voicerecordingapp.record_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import anonymous.amdeveloper.voicerecordingapp.R
import anonymous.amdeveloper.voicerecordingapp.database.RecordDatabase
import anonymous.amdeveloper.voicerecordingapp.databinding.FragmentRecordListBinding

class RecordListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentRecordListBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_record_list, container, false
        )
        val application = requireNotNull(this.activity).application

        val dataSource = RecordDatabase.getInstance(application).recordDatabaseDao
        val viewModelFactory = RecordListViewModelFactory(dataSource)

        val listRecordViewModel = ViewModelProviders.of(
            this, viewModelFactory).get(RecordListViewModel::class.java)

        binding.recordListViewModel = listRecordViewModel

        val adapter = RecordListAdapter()
        binding.recyclerView.adapter = adapter

        listRecordViewModel.records.observe(viewLifecycleOwner) {
            it?.let { adapter.data = it }
        }

        binding.lifecycleOwner = this

        return binding.root
    }

}