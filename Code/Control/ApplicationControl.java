package Control;

import java.util.List;
import Entity.Application;

public class ApplicationControl {
	private List<String> pendingWithdrawnApplicationID;
	private AuthenticationControl authCtrl;

	public ApplicationControl(AuthenticationControl authCtrl) {
		// implementation
	}

	public void loadApplicationFromDB() {
		// implementation
	}

	public String getApplicationStatus(Application app) {
		// implementation
		return null;
	}

	public void makeApplication() {
		// implementation
	}

	public void requestWithdrawApplication(Application app) {
		// implementation
	}

	public List<Application> tendingWithdrawal(Application app) {
		// implementation
		return null;
	}

	public void approveWithdrawal(Application app) {
		// implementation
	}

	public void rejectWithdrawal(Application app) {
		// implementation
	}

	public void addApplicationToPendingList(Application app) {
		// implementation
	}

	public void removeApplicationToPendingList(Application app) {
		// implementation
	}
}
