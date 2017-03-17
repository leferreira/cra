/**
 * JAVA SCRIPTS DA DATA TABLE EDITABLE
 *
 */
 function initCraDatatable() {
	$('.navigatorLabel>div').each(function() {
        var text = $(this).text();
        text = text.replace('Showing', 'Mostrando');
        text = text.replace('to', 'até');
		text = text.replace('of', 'de');
		text = text.concat(' registros');
        $(this).text(text);
    });
}