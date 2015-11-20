package com.nyavro.manythanks;

import android.net.Uri;
import android.provider.ContactsContract;

public class Contacts {
    public static final Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
    public static final String DISPLAY_NAME = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;
    public static final String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
    public static final String ID = ContactsContract.Contacts._ID;
    public static final Uri PHONE_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
    public static final String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
    public static final String PHONE_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
}
