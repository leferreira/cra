 $(document).ready(function() {
        $('#table').dataTable( {
            "language": {
					"sEmptyTable": "Nenhum registro encontrado",
					    "sInfo": "Mostrando de _START_ até _END_ de _TOTAL_ registros",
					    "sInfoEmpty": "Mostrando 0 até 0 de 0 registros",
					    "sInfoFiltered": "(Filtrados de _MAX_ registros)",
					    "sInfoPostFix": "",
					    "sInfoThousands": ".",
					    "sLengthMenu": "_MENU_  Resultados por página",
					    "sLoadingRecords": "Carregando...",
					    "sProcessing": "Processando...",
					    "sZeroRecords": "Nenhum registro encontrado",
					    "sSearch": " Pesquisar ",
					    "oPaginate": {
					        "sNext": "Próximo",
					        "sPrevious": "Anterior",
					        "sFirst": "Primeiro",
					        "sLast": "Último"
					    },
					    "oAria": {
					        "sSortAscending": ": Ordenar colunas de forma ascendente",
					        "sSortDescending": ": Ordenar colunas de forma descendente"
					    }
            }
        } );
        jQuery(function($){
            $.datepicker.regional['pt-BR'] = {
                    closeText: 'Fechar',
                    prevText: '&#x3c;Anterior',
                    nextText: 'Pr&oacute;ximo&#x3e;',
                    currentText: 'Hoje',
                    monthNames: ['Janeiro','Fevereiro','Mar&ccedil;o','Abril','Maio','Junho','Julho','Agosto','Setembro','Outubro','Novembro','Dezembro'],
                    monthNamesShort: ['Jan','Fev','Mar','Abr','Mai','Jun','Jul','Ago','Set','Out','Nov','Dez'],
                    dayNames: ['Domingo','Segunda-feira','Ter&ccedil;a-feira','Quarta-feira','Quinta-feira','Sexta-feira','Sabado'],
                    dayNamesShort: ['Dom','Seg','Ter','Qua','Qui','Sex','Sab'],
                    dayNamesMin: ['Dom','Seg','Ter','Qua','Qui','Sex','Sab'],
                    weekHeader: 'Sm',
                    dateFormat: 'dd/mm/yy',
                    firstDay: 0,
                    isRTL: false,
                    showMonthAfterYear: false,
                    yearSuffix: ''};
            $.datepicker.setDefaults($.datepicker.regional['pt-BR']);
        });
    } );