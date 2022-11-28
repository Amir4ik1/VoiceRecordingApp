package anonymous.amdeveloper.voicerecordingapp.record_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import anonymous.amdeveloper.voicerecordingapp.database.RecordDatabaseDao

class RecordListViewModelFactory (
    private val databaseDao: RecordDatabaseDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecordListViewModel::class.java)) {
            return RecordListViewModel(databaseDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}