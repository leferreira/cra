package br.com.ieptbto.cra.component.dataTable;

/**
 * @author Thasso Araújo
 *
 */
public enum TypeCraButton {

	DOWNLOAD_FILE("col-center link", "img/download.jpg"), 
	RETURN("col-center fa fa-mail-reply", ""), 
	REMOVE("col-center fa fa-trash-o", ""), 
	REPORT("col-center fa fa-print", ""),
	ATTACHMENT("col-center link", "img/clipe.png");
	
	private String htmlClass;
	private String srcImage;
	
	private TypeCraButton(String htmlClass, String srcImage) {
		this.htmlClass = htmlClass;
		this.srcImage = srcImage;
	}
	
	public String getHtmlClass() {
		return htmlClass;
	}
	
	public String getSrcImage() {
		return srcImage;
	}
}