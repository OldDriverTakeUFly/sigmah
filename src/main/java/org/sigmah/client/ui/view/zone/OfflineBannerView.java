package org.sigmah.client.ui.view.zone;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import org.sigmah.client.ui.presenter.zone.OfflineBannerPresenter;
import org.sigmah.client.ui.view.base.AbstractView;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import org.sigmah.client.ui.res.icon.offline.OfflineIconBundle;
import org.sigmah.client.ui.widget.RatioBar;
import org.sigmah.offline.status.ApplicationState;
import org.sigmah.offline.view.OfflineMenuPanel;
import org.sigmah.offline.view.SynchronizePopup;

/**
 * Offline banner view (not a real view, just a widget set).
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class OfflineBannerView extends AbstractView implements OfflineBannerPresenter.View {

	private Panel statusPanel;
	private FlowPanel menuHandle;
    private RatioBar progressBar;
    private OfflineMenuPanel menuPanel;
    private SynchronizePopup synchronizePopup;
    private Image warningIcon;
    private Image onlineIcon;
    private Image signalIcon;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {

		statusPanel = new FlowPanel();
		statusPanel.getElement().setId("offline-status");
        
        warningIcon = new Image(OfflineIconBundle.INSTANCE.error());
        warningIcon.setVisible(false);
		statusPanel.add(warningIcon);
        
        onlineIcon = new Image(OfflineIconBundle.INSTANCE.connect());
        signalIcon = new Image(OfflineIconBundle.INSTANCE.signalOn());

		menuHandle = new FlowPanel();
		menuHandle.addStyleName("offline-button");
		menuHandle.add(signalIcon);
		menuHandle.add(onlineIcon);
		statusPanel.add(menuHandle);
        
        progressBar = new RatioBar(0);
        progressBar.setStyleName("offline-progress");
        progressBar.getElement().getStyle().setDisplay(Style.Display.NONE);
        statusPanel.add(progressBar);
		
		menuPanel = new OfflineMenuPanel();
        menuPanel.setVisible(false);
		statusPanel.add(menuPanel);
        
        synchronizePopup = new SynchronizePopup();
		
		// initWidget(); Useless.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onViewRevealed() {
		// Nothing to do here.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Panel getStatusPanel() {
		return statusPanel;
	}
    
    /**
	 * {@inheritDoc}
	 */
    @Override
    public Panel getMenuHandle() {
        return menuHandle;
    }

    /**
	 * {@inheritDoc}
	 */
    @Override
    public OfflineMenuPanel getMenuPanel() {
        return menuPanel;
    }

    @Override
    public SynchronizePopup getSynchronizePopup() {
        return synchronizePopup;
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setStatus(ApplicationState state) {
        switch(state) {
            case ONLINE:
				onlineIcon.setResource(OfflineIconBundle.INSTANCE.connect());
				signalIcon.setResource(OfflineIconBundle.INSTANCE.signalOn());
                break;
				
			case READY_TO_SYNCHRONIZE:
				onlineIcon.setResource(OfflineIconBundle.INSTANCE.disconnect());
				signalIcon.setResource(OfflineIconBundle.INSTANCE.signalOn());
				break;
				
			case OFFLINE:
				onlineIcon.setResource(OfflineIconBundle.INSTANCE.disconnect());
				signalIcon.setResource(OfflineIconBundle.INSTANCE.signalOff());
				break;
                
            default:
                break;
        }
	}

    @Override
    public void setProgress(double progress, boolean undefined) {
		if(undefined) {
			progressBar.getElement().getStyle().clearDisplay();
			progressBar.setRatioUndefined();
			
		} else if(progress <= 0 || progress >= 1) {
            progressBar.getElement().getStyle().setDisplay(Style.Display.NONE);
			
        } else {
            progressBar.getElement().getStyle().clearDisplay();
            progressBar.setRatio(progress * 100.0);
        }
    }
	
    @Override
    public void setSynchronizeAnchorEnabled(boolean enabled) {
		setAnchorEnabled(menuPanel.getUpdateDatabaseAnchor(), enabled);
    }
	
    @Override
    public void setTransferFilesAnchorEnabled(boolean enabled) {
		setAnchorEnabled(menuPanel.getTransferFilesAnchor(), enabled);
    }
	
	@Override
	public boolean isEnabled(Anchor anchor) {
		return !anchor.getStyleName().contains("offline-action-disabled");
	}
	
	private void setAnchorEnabled(Anchor anchor, boolean enabled) {
        anchor.setEnabled(enabled);
        if(enabled) {
            anchor.removeStyleName("offline-action-disabled");
        } else {
            anchor.addStyleName("offline-action-disabled");
        }
	}
    
    @Override
    public void setWarningIconVisible(boolean visible) {
        warningIcon.setVisible(visible);
    }
    
}
