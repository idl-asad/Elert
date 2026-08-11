package com.example.elert.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.ContactsContract
import androidx.core.content.ContextCompat
import com.example.elert.data.model.DeviceContact

object ContactsProvider {

    fun hasContactsPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun loadContacts(context: Context): List<DeviceContact> {
        if (!hasContactsPermission(context)) return emptyList()

        val contacts = LinkedHashMap<String, DeviceContact>()
        val resolver = context.contentResolver
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID
        )

        resolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val idIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
            if (nameIndex < 0 || idIndex < 0) return emptyList()

            while (cursor.moveToNext()) {
                val name = cursor.getString(nameIndex)?.trim().orEmpty()
                val id = cursor.getString(idIndex).orEmpty()
                if (name.isEmpty() || id.isEmpty()) continue
                contacts[id] = DeviceContact(id = id, displayName = name)
            }
        }

        return contacts.values.sortedBy { it.displayName.lowercase() }
    }
}
