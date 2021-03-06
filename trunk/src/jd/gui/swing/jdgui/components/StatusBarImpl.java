//    jDownloader - Downloadmanager
//    Copyright (C) 2009  JD-Team support@jdownloader.org
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

package jd.gui.swing.jdgui.components;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import jd.Main;
import jd.controlling.linkchecker.LinkChecker;
import jd.controlling.linkchecker.LinkCheckerEvent;
import jd.controlling.linkchecker.LinkCheckerListener;
import jd.controlling.linkcollector.LinkCollector;
import jd.controlling.linkcrawler.LinkCrawler;
import jd.controlling.linkcrawler.LinkCrawlerEvent;
import jd.controlling.linkcrawler.LinkCrawlerListener;
import jd.controlling.reconnect.Reconnecter;
import jd.gui.swing.jdgui.components.premiumbar.PremiumStatus;
import jd.gui.swing.laf.LookAndFeelController;
import net.miginfocom.swing.MigLayout;

import org.appwork.controlling.StateEvent;
import org.appwork.controlling.StateEventListener;
import org.appwork.utils.swing.EDTRunner;
import org.jdownloader.actions.AppAction;
import org.jdownloader.gui.translate._GUI;
import org.jdownloader.images.NewTheme;

public class StatusBarImpl extends JPanel {

    private static final long      serialVersionUID = 3676496738341246846L;
    private ReconnectProgress      reconnectIndicator;
    private IconedProcessIndicator linkGrabberIndicator;

    public StatusBarImpl() {
        Main.GUI_COMPLETE.executeWhenReached(new Runnable() {

            public void run() {
                new EDTRunner() {

                    @Override
                    protected void runInEDT() {
                        initGUI();
                    }
                };

            }

        });

    }

    private void initGUI() {
        setLayout(new MigLayout("ins 0", "[fill,grow,left][][][]3", "[22!]"));
        if (LookAndFeelController.getInstance().getLAFOptions().isPaintStatusbarTopBorder()) {
            setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, getBackground().darker()));
        } else {
            setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, getBackground().darker()));
        }

        // spMaxSpeed = new JDSpinner(_GUI._.gui_statusbar_speed());
        // spMaxSpeed.getSpinner().addChangeListener(this);
        // spMaxSpeed.getSpinner().setModel(new SpinnerNumberModel(0, 0,
        // Integer.MAX_VALUE, 50));
        // try {
        // spMaxSpeed.setValue(JsonConfig.create(GeneralSettings.class).getDownloadSpeedLimit());
        // } catch (Throwable e) {
        // spMaxSpeed.setValue(0);
        // // dlConfig.setProperty(Configuration.PARAM_DOWNLOAD_MAX_SPEED, 0);
        // //
        // // dlConfig.save();
        // JsonConfig.create(GeneralSettings.class).setDownloadSpeedLimit(0);
        // }
        // spMaxSpeed.setToolTipText(_GUI._.gui_tooltip_statusbar_speedlimiter());
        // colorizeSpinnerSpeed();
        //
        // spMaxDls = new JDSpinner(_GUI._.gui_statusbar_sim_ownloads(),
        // "h 20!");
        // spMaxDls.getSpinner().setModel(new SpinnerNumberModel(2, 1, 20, 1));
        // try {
        // spMaxDls.setValue(JsonConfig.create(GeneralSettings.class).getMaxSimultaneDownloads());
        // } catch (Throwable e) {
        // spMaxDls.setValue(2);
        // // dlConfig.setProperty(Configuration.PARAM_DOWNLOAD_MAX_SIMULTAN,
        // // 2);
        // // dlConfig.save();
        // JsonConfig.create(GeneralSettings.class).setMaxSimultaneDownloads(2);
        // }
        // spMaxDls.setToolTipText(_GUI._.gui_tooltip_statusbar_simultan_downloads());
        // spMaxDls.getSpinner().addChangeListener(this);
        //
        // spMaxChunks = new JDSpinner(_GUI._.gui_statusbar_maxChunks(),
        // "h 20!");
        // spMaxChunks.getSpinner().setModel(new SpinnerNumberModel(2, 1, 20,
        // 1));
        // try {
        // spMaxChunks.setValue(JsonConfig.create(GeneralSettings.class).getMaxChunksPerFile());
        // } catch (Throwable e) {
        // // dlConfig.setProperty(Configuration.PARAM_DOWNLOAD_MAX_CHUNKS, 2);
        // // dlConfig.save();
        // JsonConfig.create(GeneralSettings.class).setMaxChunksPerFile(2);
        // }
        // spMaxChunks.setToolTipText(_GUI._.gui_tooltip_statusbar_max_chunks());
        // spMaxChunks.getSpinner().addChangeListener(this);

        super.add(PremiumStatus.getInstance());

        reconnectIndicator = new ReconnectProgress();
        // IconedProcessIndicator;
        reconnectIndicator.setTitle(_GUI._.StatusBarImpl_initGUI_reconnect());
        reconnectIndicator.setIndeterminate(false);
        reconnectIndicator.setEnabled(false);
        Main.GUI_COMPLETE.executeWhenReached(new Runnable() {

            public void run() {
                Reconnecter.getInstance().getStateMachine().addListener(new StateEventListener() {

                    public void onStateChange(StateEvent event) {
                        boolean r = false;
                        if (event.getNewState() == Reconnecter.RECONNECT_RUNNING) {
                            r = true;
                        }
                        final boolean running = r;
                        new EDTRunner() {
                            @Override
                            protected void runInEDT() {
                                reconnectIndicator.setEnabled(running);
                                reconnectIndicator.setIndeterminate(running);
                            }
                        };
                    }

                    public void onStateUpdate(StateEvent event) {
                    }

                });
            }

        });

        // reconnectIndicator.setToolTipText("<html><img src=\"" +
        // NewTheme.I().getImageUrl("reconnect") +
        // "\"></img>Waiting for new IP - Reconnect in progress</html>");

        linkGrabberIndicator = new IconedProcessIndicator(NewTheme.I().getIcon("linkgrabber", 16));
        linkGrabberIndicator.setTitle(_GUI._.StatusBarImpl_initGUI_linkgrabber());
        linkGrabberIndicator.setDescription(_GUI._.StatusBarImpl_initGUI_linkgrabber_desc_inactive());
        linkGrabberIndicator.setIndeterminate(false);
        linkGrabberIndicator.setEnabled(false);
        linkGrabberIndicator.addMouseListener(new MouseListener() {

            public void mouseReleased(MouseEvent e) {
                final JPopupMenu popup = new JPopupMenu();

                popup.add(new AppAction() {
                    /**
                     * 
                     */
                    private static final long serialVersionUID = -968768342263254431L;

                    {
                        this.setIconKey("cancel");
                        this.setName(_GUI._.StatusBarImpl_initGUI_abort_linkgrabber());
                        this.setEnabled(linkGrabberIndicator.isEnabled());
                    }

                    public void actionPerformed(ActionEvent e) {
                        LinkCollector.getInstance().abort();
                    }

                });

                popup.show(linkGrabberIndicator, e.getPoint().x, 0 - popup.getPreferredSize().height);
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseClicked(MouseEvent e) {
            }
        });
        Main.GUI_COMPLETE.executeWhenReached(new Runnable() {

            public void run() {
                LinkCrawler.getEventSender().addListener(new LinkCrawlerListener() {

                    public void onLinkCrawlerEvent(LinkCrawlerEvent event) {
                        updateLinkGrabberIndicator();
                    }

                });
                LinkChecker.getEventSender().addListener(new LinkCheckerListener() {

                    public void onLinkCheckerEvent(LinkCheckerEvent event) {
                        updateLinkGrabberIndicator();
                    }

                });

            }

        });

        // linkGrabberIndicator.setToolTipText("<html><img src=\"" +
        // NewTheme.I().getImageUrl("linkgrabber") +
        // "\"></img>Crawling for Downloads</html>");

        // extractIndicator.setToolTipText("<html><img src=\"" +
        // NewTheme.I().getImageUrl("archive") +
        // "\"></img>Extracting Archives: 85%</html>");

        super.add(Box.createHorizontalGlue(), "height 22!,width 22!");
        super.add(reconnectIndicator, "height 22!,width 22!");
        super.add(linkGrabberIndicator, "height 22!,width 22!");

        // add(extractIndicator, "height 22!,width 22!,hidemode 2");
    }

    public IconedProcessIndicator getLinkGrabberIndicator() {
        return linkGrabberIndicator;
    }

    public Component add(Component comp) {
        updateLayout(getComponentCount() + 1);
        try {
            return super.add(comp);
        } finally {
            revalidate();
        }
    }

    private void updateLayout(int components) {
        StringBuilder sb = new StringBuilder();
        sb.append("[fill,grow,left][22!][22!]");
        for (int i = 4; i < components; i++) {
            sb.append("[22!]");
        }
        sb.append("3");
        setLayout(new MigLayout("ins 0", sb.toString(), "[22!]"));
    }

    public void remove(Component comp) {
        updateLayout(getComponentCount() - 1);
        super.remove(comp);
        revalidate();
    }

    public void add(Component comp, Object constraints) {
        updateLayout(getComponentCount() + 1);
        super.add(comp, constraints);
        revalidate();
    }

    private void updateLinkGrabberIndicator() {
        final boolean enabled = LinkChecker.isChecking() || LinkCrawler.isCrawling();
        new EDTRunner() {
            @Override
            protected void runInEDT() {
                linkGrabberIndicator.setEnabled(enabled);
                linkGrabberIndicator.setIndeterminate(enabled);
                if (enabled) {
                    linkGrabberIndicator.setDescription(_GUI._.StatusBarImpl_initGUI_linkgrabber_desc());
                } else {
                    linkGrabberIndicator.setDescription(_GUI._.StatusBarImpl_initGUI_linkgrabber_desc_inactive());
                }
            }
        };
    }

}