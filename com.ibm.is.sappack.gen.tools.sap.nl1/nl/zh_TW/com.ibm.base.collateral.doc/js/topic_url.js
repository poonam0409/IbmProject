// Writes this.location without parameters or link targets.
// (C) Copyright IBM Corporation 2011
function write_topic_url() {
	var loc = new String(this.location);
	var pos = loc.indexOf("#");
	if (pos != -1) {
		loc = loc.substr(0, pos);
	} else {
		pos = loc.indexOf("?");
		if (pos != -1) {
			loc = loc.substr(0, pos);
		}
	}
	document.write(loc);
}
