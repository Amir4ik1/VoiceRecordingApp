package anonymous.amdeveloper.voicerecordingapp.remove_dialog

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import anonymous.amdeveloper.voicerecordingapp.database.RecordDatabaseDao

class RemoveDialogViewModelFactory(
    private val databaseDao: RecordDatabaseDao,
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RemoveDialogViewModel::class.java)) {
            return RemoveDialogViewModel(
                databaseDao,
                application
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}