package com.nyavro.manythanks;

import android.net.Uri;
import android.provider.ContactsContract;

public class Contacts {
    public static final String DISPLAY_NAME = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;
    public static final String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
    public static final String ID = ContactsContract.Contacts._ID;
    public static final Uri CONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
}
