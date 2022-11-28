package anonymous.amdeveloper.voicerecordingapp.record

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Environment.DIRECTORY_RECORDINGS
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import anonymous.amdeveloper.voicerecordingapp.MainActivity
import anonymous.amdeveloper.voicerecordingapp.R
import anonymous.amdeveloper.voicerecordingapp.database.RecordDatabase
import anonymous.amdeveloper.voicerecordingapp.database.RecordDatabaseDao
import anonymous.amdeveloper.voicerecordingapp.databinding.FragmentRecordBinding
import kotlinx.android.synthetic.main.fragment_record.*
import java.io.File

class RecordFragment : Fragment() {

    private lateinit var viewModel: RecordViewModel
    private lateinit var mainActivity: MainActivity
    private var count: Int? = null
    private var database: RecordDatabaseDao? = null
    private val MY_PERMISSIONS_RECORD_AUDIO = 123

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentRecordBinding>(
            inflater,
            R.layout.fragment_record,
            container,
            false
        )

        database = requireContext().let { RecordDatabase.getInstance(it).recordDatabaseDao }

        mainActivity = activity as MainActivity

        viewModel = ViewModelProvider(this).get(RecordViewModel::class.java)

        binding.recordViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        if (!mainActivity.isServiceRunning()) viewModel.resetTimer()
        else binding.playButton.setImageResource(R.drawable.ic_baseline_stop_36dp)

        binding.playButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(),
                android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(android.Manifest.permission.RECORD_AUDIO), MY_PERMISSIONS_RECORD_AUDIO
                )
            } else {
                if (mainActivity.isServiceRunning()) {
                    onRecord(false)
                    viewModel.stopTimer()
                } else {
                    onRecord(true)
                    viewModel.startTimer()
                }
            }
        }

        createChannel(
            getString(R.string.notification_channel_id),
            getString(R.string.notification_channel_name)
        )

        return binding.root
    }

    private fun onRecord(start: Boolean) {
        val intent: Intent = Intent(requireActivity(), RecordService::class.java)

        if (start) {
            playButton.setImageResource(R.drawable.ic_baseline_stop_36dp)
            Toast.makeText(requireContext(), R.string.toast_recording_start, Toast.LENGTH_SHORT).show()

            val folder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                File(Environment.getExternalStoragePublicDirectory(DIRECTORY_RECORDINGS)?.absolutePath.toString() + "/VoiceRecorder")
            } else {
                File(Environment.getExternalStoragePublicDirectory(null)?.absolutePath.toString() + "/VoiceRecorder")
            }
            if (!folder.exists()) {
                folder.mkdir()
            }

            requireActivity().startService(intent)
            requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            playButton.setImageResource(R.drawable.ic_micro_white_36dp)

            requireActivity().stopService(intent)
            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode) {
            MY_PERMISSIONS_RECORD_AUDIO -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onRecord(true)
                    viewModel.startTimer()
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.toast_recording_permissions),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return
            }
        }
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
                .apply {
                    setShowBadge(false)
                    setSound(null, null)
                }
            val notificationManager = requireActivity().getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

}