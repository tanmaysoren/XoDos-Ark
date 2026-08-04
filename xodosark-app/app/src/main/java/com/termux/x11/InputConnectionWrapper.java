package com.termux.x11;

import android.os.Bundle;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.CorrectionInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;

/**
 * Wrapper for InputConnection to provide compatibility layer.
 * Delegates all calls to the underlying InputConnection.
 */
public class InputConnectionWrapper implements InputConnection {
    private final InputConnection mWrapped;

    public InputConnectionWrapper(InputConnection wrapped) {
        mWrapped = wrapped;
    }

    @Override
    public CharSequence getTextBeforeCursor(int length, int flags) {
        return mWrapped != null ? mWrapped.getTextBeforeCursor(length, flags) : null;
    }

    @Override
    public CharSequence getTextAfterCursor(int length, int flags) {
        return mWrapped != null ? mWrapped.getTextAfterCursor(length, flags) : null;
    }

    @Override
    public CharSequence getSelectedText(int flags) {
        return mWrapped != null ? mWrapped.getSelectedText(flags) : null;
    }

    @Override
    public int getCursorCapsMode(int reqModes) {
        return mWrapped != null ? mWrapped.getCursorCapsMode(reqModes) : 0;
    }

    @Override
    public ExtractedText getExtractedText(ExtractedTextRequest request, int flags) {
        return mWrapped != null ? mWrapped.getExtractedText(request, flags) : null;
    }

    @Override
    public boolean deleteSurroundingText(int beforeLength, int afterLength) {
        return mWrapped != null && mWrapped.deleteSurroundingText(beforeLength, afterLength);
    }

    @Override
    public boolean setComposingText(CharSequence text, int newCursorPosition) {
        return mWrapped != null && mWrapped.setComposingText(text, newCursorPosition);
    }

    @Override
    public boolean finishComposingText() {
        return mWrapped != null && mWrapped.finishComposingText();
    }

    @Override
    public boolean commitText(CharSequence text, int newCursorPosition) {
        return mWrapped != null && mWrapped.commitText(text, newCursorPosition);
    }

    @Override
    public boolean commitCompletion(CompletionInfo text) {
        return mWrapped != null && mWrapped.commitCompletion(text);
    }

    @Override
    public boolean commitCorrection(CorrectionInfo correctionInfo) {
        return mWrapped != null && mWrapped.commitCorrection(correctionInfo);
    }

    @Override
    public boolean setSelection(int start, int end) {
        return mWrapped != null && mWrapped.setSelection(start, end);
    }

    @Override
    public boolean performEditorAction(int actionCode) {
        return mWrapped != null && mWrapped.performEditorAction(actionCode);
    }

    @Override
    public boolean performContextMenuAction(int id) {
        return mWrapped != null && mWrapped.performContextMenuAction(id);
    }

    @Override
    public boolean beginBatchEdit() {
        return mWrapped != null && mWrapped.beginBatchEdit();
    }

    @Override
    public boolean endBatchEdit() {
        return mWrapped != null && mWrapped.endBatchEdit();
    }

    @Override
    public boolean sendKeyEvent(android.view.KeyEvent event) {
        return mWrapped != null && mWrapped.sendKeyEvent(event);
    }

    @Override
    public boolean clearMetaKeyStates(int states) {
        return mWrapped != null && mWrapped.clearMetaKeyStates(states);
    }

    @Override
    public boolean reportFullscreenMode(boolean enabled) {
        return mWrapped != null && mWrapped.reportFullscreenMode(enabled);
    }

    @Override
    public boolean performPrivateCommand(String action, Bundle data) {
        return mWrapped != null && mWrapped.performPrivateCommand(action, data);
    }

    @Override
    public boolean requestCursorUpdates(int cursorUpdateMode) {
        return mWrapped != null && mWrapped.requestCursorUpdates(cursorUpdateMode);
    }
}
