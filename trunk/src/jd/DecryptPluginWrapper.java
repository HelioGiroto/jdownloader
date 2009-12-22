//    jDownloader - Downloadmanager
//    Copyright (C) 2008  JD-Team support@jdownloader.org
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.

package jd;

import java.util.ArrayList;

import jd.controlling.JDLogger;
import jd.nutils.Formatter;
import jd.plugins.PluginForDecrypt;

public class DecryptPluginWrapper extends PluginWrapper {
    private static final ArrayList<DecryptPluginWrapper> DECRYPT_WRAPPER = new ArrayList<DecryptPluginWrapper>();
    private static volatile boolean uninitialized = true;
    public static final Object LOCK = new Object();

    public static ArrayList<DecryptPluginWrapper> getDecryptWrapper() {
        synchronized (LOCK) {
            if (uninitialized) {
                try {
                    JDInit.loadPluginForDecrypt();
                } catch (Throwable e) {
                    JDLogger.exception(e);
                }
                uninitialized = false;
            }
            return DECRYPT_WRAPPER;
        }
    }

    private final String revision;

    public DecryptPluginWrapper(final String host, final String classNamePrefix, final String className, final String patternSupported, final int flags, final String revision) {
        super(host, classNamePrefix, className, patternSupported, flags);
        this.revision = Formatter.getRevision(revision);
        synchronized (LOCK) {
            DECRYPT_WRAPPER.add(this);
        }
    }

    // public DecryptPluginWrapper(String host, String className, String
    // patternSupported) {
    // this(host, "jd.plugins.decrypter.", className, patternSupported, 0);
    // }

    public DecryptPluginWrapper(final String host, final String className, final String patternSupported, final int flags, final String revision) {
        this(host, "jd.plugins.decrypter.", className, patternSupported, flags, revision);
    }

    // @Override
    public PluginForDecrypt getPlugin() {
        return (PluginForDecrypt) super.getPlugin();
    }

    @Override
    public String getVersion() {
        return revision;
    }

    public static boolean hasPlugin(final String s) {
        for (DecryptPluginWrapper w : getDecryptWrapper()) {
            if (w.canHandle(s)) return true;
        }
        return false;
    }

}
