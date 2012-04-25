package edu.ucsf.vivo.opensocial;

public class GadgetViewRequirements {
	private String page;
	private char viewerReq; // U for User or null for no requirement
	private char ownerReq; // R for Registered or null for no requirement
	private String view;
	private int closedWidth;
	private int openWidth;
	private boolean startClosed;
	private String chromeId;
	private int display_order;

	public GadgetViewRequirements(String page, char viewerReq, char ownerReq,
			String view, int closedWidth, int openWidth, boolean startClosed,
			String chromeId, int display_order) {
		this.page = page;
		this.viewerReq = viewerReq;
		this.ownerReq = ownerReq;
		this.view = view;
		this.closedWidth = closedWidth;
		this.openWidth = openWidth;
		this.startClosed = startClosed;
		this.chromeId = chromeId;
		this.display_order = display_order;
	}

	public GadgetViewRequirements(String page, String viewerReq,
			String ownerReq, String view, int closedWidth, int openWidth,
			boolean startClosed, String chromeId, int display_order) {
		this(page, viewerReq != null ? viewerReq.charAt(0) : ' ',
				ownerReq != null ? ownerReq.charAt(0) : ' ', view, closedWidth,
				openWidth, startClosed, chromeId, display_order);
	}

	public char getViewerReq() {
		return viewerReq;
	}

	public char getOwnerReq() {
		return ownerReq;
	}

	public String getView() {
		return view;
	}

	public int getClosedWidth() {
		return closedWidth;
	}

	public int getOpenWidth() {
		return openWidth;
	}

	public boolean getStartClosed() {
		return startClosed;
	}

	public String getChromeId() {
		return chromeId;
	}

	int getDisplayOrder() {
		return display_order;
	}
}