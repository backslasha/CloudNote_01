package mouse.com.cloudnote_01.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import mouse.com.cloudnote_01.frags.EmptyFragment;


public class EmptyUtils {

    public static void startEdit(FragmentManager fragmentManager, int container_id) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(container_id, new EmptyFragment());
        transaction.commit();
    }

    public static void finishEdit(FragmentManager fragmentManager, Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.remove(fragment);
        transaction.commit();
    }
}
