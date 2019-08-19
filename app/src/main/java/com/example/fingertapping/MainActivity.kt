package com.example.fingertapping

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View

import com.google.common.collect.Lists

import java.util.ArrayList
import java.util.Collections

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        grantPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        grantPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
        grantPermission(Manifest.permission.INTERNET)
    }

    private fun grantPermission(permission: String) {
        if (ContextCompat.checkSelfPermission(this,
                        permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(permission),
                    1)

        }
    }


    fun tapTwoClicked(view: View) {
        val measuresRight = generateMeasurementsOrder()
        val measuresLeft = generateMeasurementsOrder()
        val order = createMeasurementsOrderArray(measuresRight, measuresLeft)
        val i = Intent(baseContext, UserDataActivity::class.java)
        i.putExtra("order", order)
        startActivity(i)
    }

    private fun createMeasurementsOrderArray(measuresRight: ArrayList<Int>, measuresLeft: ArrayList<Int>): IntArray {
        val measurementsOrder = IntArray(6)
        measurementsOrder[0] = measuresRight[0]
        measurementsOrder[1] = measuresRight[1]
        measurementsOrder[2] = measuresRight[2]
        measurementsOrder[3] = measuresLeft[0]
        measurementsOrder[4] = measuresLeft[1]
        measurementsOrder[5] = measuresLeft[2]
        return measurementsOrder
    }

    private fun generateMeasurementsOrder(): ArrayList<Int> {
        val measurements = Lists.newArrayList(0, 1, 2)
        measurements.shuffle()
        return measurements
    }


    fun settingsClicked(view: View) {
        val i = Intent(baseContext, SettingsActivity::class.java)
        startActivity(i)
    }
}
