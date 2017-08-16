package com.kamron.pogoiv;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.google.common.base.Strings;
import com.kamron.pogoiv.clipboard.ClipboardToken;
import com.kamron.pogoiv.clipboard.tokens.IVPercentageToken;
import com.kamron.pogoiv.clipboard.tokens.IVPercentageTokenMode;
import com.kamron.pogoiv.clipboard.tokens.PokemonNameToken;
import com.kamron.pogoiv.clipboard.tokens.SeparatorToken;
import com.kamron.pogoiv.clipboard.tokens.UnicodeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A singleton class which is used to access and modify the "persistent" settings saved in phone memory.
 */
public class GoIVSettings {

    public static final String PREFS_GO_IV_SETTINGS = "GoIV_settings";
    public static final String LAUNCH_POKEMON_GO = "launchPokemonGo";
    public static final String SHOW_CONFIRMATION_DIALOG = "showConfirmationDialog";
    public static final String MANUAL_SCREENSHOT_MODE = "manualScreenshotMode";
    public static final String DELETE_SCREENSHOTS = "deleteScreenshots";
    public static final String COPY_TO_CLIPBOARD = "copyToClipboard";
    public static final String COPY_TO_CLIPBOARD_SINGLE = "copyToClipboardSingle";
    public static final String SEND_CRASH_REPORTS = "sendCrashReports";
    public static final String AUTO_UPDATE_ENABLED = "autoUpdateEnabled";
    public static final String POKESPAM_ENABLED = "pokeSpamEnabled";
    public static final String TEAM_NAME = "teamName";
    public static final String APPRAISAL_WINDOW_POSITION = "appraisalWindowPosition";
    public static final String GOIV_CLIPBOARDSETTINGS = "GoIV_ClipboardSettings";
    public static final String GOIV_CLIPBOARDSINGLESETTINGS = "GoIV_ClipboardSingleSettings";
    public static final String SHOW_TRANSLATED_POKEMON_NAME = "showTranslatedPokemonName";
    public static final String HAS_WARNED_USER_NO_SCREENREC = "GOIV_hasWarnedUserNoScreenRec";
    public static final String COPY_TO_CLIPBOARD_SHOW_TOAST = "copyToClipboardShowToast";
    public static final String AUTO_APPRAISAL_SCAN_DELAY = "autoAppraisalScanDelay";
    public static final String AUTO_OPEN_APPRAISE_DIALOGUE = "autoOpenAppraiseDialogue";
    public static final String QUICK_IV_PREVIEW = "quick_iv_preview";
    public static final String QUICK_IV_PREVIEW_CLIPBOARD = "quick_iv_preview_clipboard";


    private static GoIVSettings instance;
    private final SharedPreferences prefs;
    private Context context;

    private GoIVSettings(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREFS_GO_IV_SETTINGS, Context.MODE_PRIVATE);
    }

    public static GoIVSettings getInstance(Context context) {
        if (instance == null) {
            instance = new GoIVSettings(context.getApplicationContext());
        }
        return instance;
    }

    public boolean shouldLaunchPokemonGo() {
        return prefs.getBoolean(LAUNCH_POKEMON_GO, true);
    }

    public boolean shouldShouldConfirmationDialogs() {
        return prefs.getBoolean(SHOW_CONFIRMATION_DIALOG, true);
    }

    public boolean isManualScreenshotModeEnabled() {
        //XXX unify with code in SettingsActivity.java
        return Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT_WATCH
                || prefs.getBoolean(MANUAL_SCREENSHOT_MODE, false);
    }

    public int playerTeam() {
        return prefs.getInt(TEAM_NAME, -1);
    }

    public void setPlayerTeam(int value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(GoIVSettings.TEAM_NAME, value);
        editor.apply();
    }

    public String getClipboardPreference() {
        String prefValue = prefs.getString(GOIV_CLIPBOARDSETTINGS, "");
        if (!Strings.isNullOrEmpty(prefValue)) {
            return prefValue;
        }

        //Below code gets the string representation of the "default" clipboard setting
        ArrayList<ClipboardToken> defaultTokens = new ArrayList<>();
        // Name (3 char) + MIN-MAX + Unicode not filled (MAX IV)
        defaultTokens.add(new PokemonNameToken(false, 3));
        defaultTokens.add(new IVPercentageToken(IVPercentageTokenMode.MIN));
        defaultTokens.add(new SeparatorToken("-"));
        defaultTokens.add(new IVPercentageToken(IVPercentageTokenMode.MAX));
        defaultTokens.add(new UnicodeToken(false));

        for (ClipboardToken token : defaultTokens) {
            prefValue += token.getStringRepresentation();
        }

        return prefValue;
    }

    public void setClipboardPreference(ArrayList<ClipboardToken> tokens) {
        SharedPreferences.Editor editor = prefs.edit();
        String saveString = "";
        for (ClipboardToken token : tokens) {
            saveString += token.getStringRepresentation();
        }
        editor.putString(GoIVSettings.GOIV_CLIPBOARDSETTINGS, saveString);
        editor.apply();
    }

    public void setClipboardSinglePreference(ArrayList<ClipboardToken> tokens) {
        //Clipboard single is the add-on setting if you want different clipboards for 1 or many results
        SharedPreferences.Editor editor = prefs.edit();
        String saveString = "";
        for (ClipboardToken token : tokens) {
            saveString += token.getStringRepresentation();
        }
        editor.putString(GoIVSettings.GOIV_CLIPBOARDSINGLESETTINGS, saveString);
        editor.apply();
    }

    public String getClipboardSinglePreference() {
        String prefValue = prefs.getString(GOIV_CLIPBOARDSINGLESETTINGS, "");
        if (!Strings.isNullOrEmpty(prefValue)) {
            return prefValue;
        }

        //Below code gets the string representation of the "default" single clipboard setting
        ArrayList<ClipboardToken> defaultTokens = new ArrayList<>();
        // Name (5 char) + AVG + Unicode not filled (MAX IV)
        defaultTokens.add(new PokemonNameToken(false, 5));
        defaultTokens.add(new IVPercentageToken(IVPercentageTokenMode.AVG));
        defaultTokens.add(new UnicodeToken(false));

        for (ClipboardToken token : defaultTokens) {
            prefValue += token.getStringRepresentation();
        }

        return prefValue;
    }


    public boolean hasShownNoScreenRecWarning() {
        return prefs.getBoolean(HAS_WARNED_USER_NO_SCREENREC, false);
    }

    public void setHasShownScreenRecWarning() {
        prefs.edit().putBoolean(HAS_WARNED_USER_NO_SCREENREC, true).apply();
    }

    public boolean shouldDeleteScreenshots() {
        return prefs.getBoolean(DELETE_SCREENSHOTS, true);
    }

    public boolean shouldCopyToClipboard() {
        return prefs.getBoolean(COPY_TO_CLIPBOARD, false);
    }

    public boolean shouldCopyToClipboardSingle() {
        return prefs.getBoolean(COPY_TO_CLIPBOARD_SINGLE, false);
    }

    public boolean shouldSendCrashReports() {
        return prefs.getBoolean(SEND_CRASH_REPORTS, true);
    }

    public boolean isAutoUpdateEnabled() {
        return prefs.getBoolean(AUTO_UPDATE_ENABLED, true);
    }

    public boolean isPokeSpamEnabled() {
        return prefs.getBoolean(POKESPAM_ENABLED, true);
    }

    public boolean shouldAutoOpenExpandedAppraise() {
        return prefs.getBoolean(AUTO_OPEN_APPRAISE_DIALOGUE, true);
    }

    public boolean shouldReplaceQuickIvPreviewWithClipboard() {
        return prefs.getBoolean(QUICK_IV_PREVIEW_CLIPBOARD, false);
    }


    public boolean isShowTranslatedPokemonName() {
        if (context.getResources().getBoolean(R.bool.use_default_pokemonsname_as_ocrstring)) {
            return prefs.getBoolean(SHOW_TRANSLATED_POKEMON_NAME, false);
        }
        return false;
    }

    public boolean shouldCopyToClipboardShowToast() {
        return prefs.getBoolean(COPY_TO_CLIPBOARD_SHOW_TOAST, true);
    }


    public boolean shouldShowQuickIVPreview() {
        return prefs.getBoolean(QUICK_IV_PREVIEW, true);
    }

    public int getAutoAppraisalScanDelay() {
        return prefs.getInt(AUTO_APPRAISAL_SCAN_DELAY, 400);
    }

    public Map<String, String> loadAppraisalCache() {
        Map<String, String> appraisalCache = new HashMap<>();

        File fileName = new File(context.getCacheDir(), "appraisalCache.ser");

        FileInputStream fis = null;
        ObjectInputStream in = null;
        try {
            fis = new FileInputStream(fileName);
            in = new ObjectInputStream(fis);
            appraisalCache = (Map<String, String>) in.readObject();
        } catch (Exception ignored) {
            //Fall-through
        } finally {

            try {
                if (fis != null) {
                    fis.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (Exception ignored) {
                //Fall-through
            }
        }

        return appraisalCache;
    }

    public void saveAppraisalCache(Map<String, String> appraisalCache) {
        File fileName = new File(context.getCacheDir(), "appraisalCache.ser");

        FileOutputStream fos = null;
        ObjectOutputStream out = null;
        try {
            fos = new FileOutputStream(fileName);
            out = new ObjectOutputStream(fos);
            out.writeObject(appraisalCache);
        } catch (Exception ignored) {
            //Fall-through
        } finally {
            try {
                if (fos != null) {
                    fos.flush();
                    fos.close();
                }
                if (out != null) {
                    out.flush();
                    out.close();
                }
            } catch (Exception ignored) {
                //Fall-through
            }
        }
    }
}
