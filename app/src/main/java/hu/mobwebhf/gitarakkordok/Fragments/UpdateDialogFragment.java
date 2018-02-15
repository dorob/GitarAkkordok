package hu.mobwebhf.gitarakkordok.Fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import hu.mobwebhf.gitarakkordok.Activities.MainActivity;
import hu.mobwebhf.gitarakkordok.Model.SongItem;
import hu.mobwebhf.gitarakkordok.Network.NetworkManager;
import hu.mobwebhf.gitarakkordok.R;

/**
 * Created by Benjamin on 2017. 10. 25..
 */

public class UpdateDialogFragment extends DialogFragment {
    FragmentManager fragmentManager;
    FragmentActivity fragmentActivity;
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        fragmentManager = getActivity().getSupportFragmentManager();
        fragmentActivity = getActivity();
        builder.setTitle(R.string.update_db);
        builder.setMessage(R.string.new_update);

        builder.setPositiveButton(R.string.download, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                UpdateLocalDBAsync();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });
        return builder.create();
    }

    private void UpdateLocalDBAsync() {
        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected void onPostExecute(Integer count) {
                super.onPostExecute(count);
                //SongItem.listAll(SongItem.class);
                //((MainActivity)getActivity()).loadItemsInBackground();
                Snackbar.make(fragmentActivity.findViewById(R.id.ContentID), String.valueOf(count) + " db új dal lett hozzáadva", Snackbar.LENGTH_LONG).show();
            }

            @Override
            protected Integer doInBackground(Void... voids) {
                return NetworkManager.UpdateLocalDB();
            }
        }.execute();
    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        final FragmentActivity activity = getActivity();
        if (activity instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }
}
