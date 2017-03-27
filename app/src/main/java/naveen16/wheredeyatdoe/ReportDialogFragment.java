package naveen16.wheredeyatdoe;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

/**
 * Created by naveen on 3/21/17.
 */

public class ReportDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        String [] options={"View Details","Report","Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select an option")
                .setItems(options,new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which){
                if(which==0){

                }

            }
        });
        builder.create().show();
//                .setPositiveButton("Report", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // FIRE ZE MISSILES!
//                    }
//                })
//                .setNeutralButton("View Details", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // FIRE ZE MISSILES!
//                    }
//                })
//                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // User cancelled the dialog
//                    }
//                });
        // Create the AlertDialog object and return it
        return builder.create();
    }


}