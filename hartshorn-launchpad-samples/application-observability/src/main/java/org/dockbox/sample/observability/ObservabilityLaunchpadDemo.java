import org.dockbox.hartshorn.launchpad.HartshornApplication;

void main(String[] args) {
    HartshornApplication.createApplication(args).initialize(app -> app.includeBasePackages(true));
}
