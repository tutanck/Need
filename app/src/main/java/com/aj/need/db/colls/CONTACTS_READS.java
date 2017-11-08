package com.aj.need.db.colls;

import com.aj.need.db.IO;
import com.aj.need.db.colls.itf.Coll;
import com.google.firebase.firestore.CollectionReference;

/**
 * Created by joan on 02/10/2017.
 */

public final class CONTACTS_READS implements Coll {

    private CONTACTS_READS() {
    }

    private final static String coll = "_MY_READ_OFFSET";


    public final static CollectionReference getCurrentUserContactReadOffsetRef(String contactID) {
        return USER_CONTACTS.getCurrentUserContactsRef().document(contactID).collection(coll);
    }

    public final static CollectionReference getContactReadOffsetRef(String contactID) {
        return USER_CONTACTS.getUserContactsRef(contactID).document(IO.getCurrentUserUid()).collection(coll);
    }
}
