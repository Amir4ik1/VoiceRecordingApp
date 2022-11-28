package anonymous.amdeveloper.voicerecordingapp.record_list

import androidx.lifecycle.ViewModel
import anonymous.amdeveloper.voicerecordingapp.database.RecordDatabaseDao

class RecordListViewModel (
    dataSource: RecordDatabaseDao
) : ViewModel() {

    val database = dataSource
    val records = database.getAllRecords()
}