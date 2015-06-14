package me.aerovulpe.ninjarunner;

import android.os.AsyncTask;

public class AsyncTaskLoader extends AsyncTask<IAsyncCallback, Integer, Boolean> {

    IAsyncCallback[] mIAsyncCallbacks;


    @Override
    protected Boolean doInBackground(IAsyncCallback... params) {
        mIAsyncCallbacks = params;

        for (IAsyncCallback param : params) {
            param.workToDo();
        }

        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        for (IAsyncCallback mIAsyncCallback : mIAsyncCallbacks) {
            mIAsyncCallback.onComplete();
        }
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
    }
}