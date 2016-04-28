package br.com.ieptbto.cra.component.dataProvider;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import br.com.ieptbto.cra.entidade.Municipio;

public class MunicipioProvider extends SortableDataProvider<Municipio, String> {

	/***/
	private static final long serialVersionUID = 1L;

	private List<Municipio> municipios;

	public MunicipioProvider(List<Municipio> municipios) {
		this.municipios = municipios;
	}

	@Override
	public Iterator<? extends Municipio> iterator(long first, long count) {
		return municipios.iterator();
	}

	@Override
	public long size() {
		return getMunicipios().size();
	}

	@Override
	public IModel<Municipio> model(final Municipio object) {
		return new AbstractReadOnlyModel<Municipio>() {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public Municipio getObject() {
				return object;
			}
		};
	}

	public List<Municipio> getMunicipios() {
		if (municipios == null) {
			municipios = new ArrayList<Municipio>();
		}
		return municipios;
	}
}
