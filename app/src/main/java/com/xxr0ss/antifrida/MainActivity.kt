package com.xxr0ss.antifrida

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.xxr0ss.antifrida.databinding.ActivityMainBinding
import com.xxr0ss.antifrida.utils.AntiFridaUtil
import com.xxr0ss.antifrida.utils.ReadVia
import com.xxr0ss.antifrida.utils.SuperUser


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val TAG = "MainActivity"

    private var read_via_pos: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        SuperUser.tryRoot(packageCodePath)
        binding.rootStatus.text = "rooted: ${SuperUser.rooted.toString()}"

        binding.spinnerVia.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                read_via_pos = pos
                Log.d(TAG, "onItemSelected: $pos $id")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.d(TAG, "onNothingSelected: null")
            }
        }

        binding.btnCheckMaps.setOnClickListener {
            // put possible frida module names here
            val blocklist = listOf("frida-agent", "frida-gadget")
            Toast.makeText(
                this,
                (if (AntiFridaUtil.checkFridaByProcMaps(blocklist, ReadVia.fromInt(read_via_pos)))
                    "frida module detected" else "No frida module detected")
                        + " via ${ReadVia.fromInt(read_via_pos).name}",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.btnCheckPort.setOnClickListener {
            Toast.makeText(
                this, if (AntiFridaUtil.checkFridaByPort(27042))
                    "frida default port 27042 detected" else "no frida default port detected",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.btnCheckProcesses.setOnClickListener {
            if (!SuperUser.rooted) {
                SuperUser.tryRoot(packageCodePath)
                if (!SuperUser.rooted)
                    return@setOnClickListener
            }
            val result = SuperUser.execRootCmd("ps -ef")
            Log.i(TAG, "Root cmd result (size ${result.length}): $result ")
            binding.tvStatus.text = result

            Toast.makeText(
                this, if (result.contains("frida-server"))
                    "frida-server process detected" else "no frida-server process found",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}