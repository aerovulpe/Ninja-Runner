package me.aerovulpe.ninjarunner;

import android.os.AsyncTask;

public class AsyncTaskLoader extends AsyncTask<IAsyncCallback, Integer, Boolean> {

    IAsyncCallback[] mIAsyncCallbacks;


    @Override
    protected Boolean doInBackground(IAsyncCallback... params) {
        mIAsyncCallbacks = params;

        for (int i = 0; i < params.length; i++) {
            params[i].workToDo();
        }

        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        for (int i = 0; i < mIAsyncCallbacks.length; i++) {
            this.mIAsyncCallbacks[i].onComplete();
        }
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
    }
}