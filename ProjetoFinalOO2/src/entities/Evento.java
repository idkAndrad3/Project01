package entities;

import java.sql.Date;
import java.sql.Time;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;

public class Evento {
	private	Integer id;
	private String titulo;
	private String local;
	private boolean isLink;
	private String descricao;
	private int capacidadeMaxima;
	private StatusEvento status;
	private CategoriaEvento categoria;

	private double preco;
	private Date dataEvento;
	private Time horaEvento;
	private Duration duracaoEvento;
	private HashMap<Integer, Administrador> organizadores;
	private HashMap<Integer, Participante> participantes;
	
	// private ADMINISTRADORRESPONSAVEL ver como vou fazer para adicionar o adm e
	// participantes
	public Evento(Integer id, String titulo, String local, boolean isLink, String descricao, int capacidadeMaxima, StatusEvento status,
			CategoriaEvento categoria, double preco, Date dataEvento, Time horaEvento, Duration duracaoEvento,
			HashMap<Integer, Administrador> organizadores, HashMap<Integer, Participante> participantes) {
		this.id = id;
		this.titulo = titulo;
		this.descricao = descricao;
		this.local = local;
		this.isLink = isLink;
		this.capacidadeMaxima = capacidadeMaxima;
		this.status = status;
		this.categoria = categoria;
		this.preco = preco;
		this.dataEvento = dataEvento;
		this.horaEvento = horaEvento;
		this.duracaoEvento = duracaoEvento;

		this.organizadores = organizadores == null ? new HashMap<>() : organizadores;;
		this.participantes = participantes== null ? new HashMap<>() : participantes;;
	}

	public void adicionarOrganizador(Administrador administrador) {
		Integer id = administrador.getId();
		this.organizadores.put(id, administrador);
	}

	public void adicionarParticipante(Participante participante) {
		Integer id = participante.getId();
		this.participantes.put(id, participante);
	}

	public boolean removerOrganizador(Integer id) {
		return this.organizadores.remove(id) != null;
	}

	public boolean removerParticipante(Integer id) {
		return this.participantes.remove(id) != null;
	}
	
	
	
	
	
	
	/*=======================GETTERS AND SETTERS=========================*/

	public Integer getId() {
		return id;
	}

	public boolean isLink() {
		return isLink;
	}

	public void setLink(boolean isLink) {
		this.isLink = isLink;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	public int getCapacidadeMaxima() {
		return capacidadeMaxima;
	}

	public void setCapacidadeMaxima(int capacidadeMaxima) {
		this.capacidadeMaxima = capacidadeMaxima;
	}

	public StatusEvento getStatus() {
		return status;
	}

	public void setStatus(StatusEvento status) {
		this.status = status;
	}

	public CategoriaEvento getCategoria() {
		return categoria;
	}

	public void setCategoria(CategoriaEvento categoria) {
		this.categoria = categoria;
	}

	public double getPreco() {
		return preco;
	}

	public void setPreco(double preco) {
		this.preco = preco;
	}

	public Date getDataEvento() {
		return dataEvento;
	}

	public void setDataEvento(Date dataEvento) {
	    LocalDate dataAtual = LocalDate.now();
	    LocalDate dataEventoInserida = dataEvento.toLocalDate();
	    
	    if (dataEventoInserida.isBefore(dataAtual)) {
	        throw new IllegalArgumentException("Data do evento não pode ser no passado.");
	    }
	    this.dataEvento = dataEvento;
	}

	public Time getHoraEvento() {
		return horaEvento;
	}

	public void setHoraEvento(Time horaEvento) {
	    // Combina data do evento com a hora fornecida
	    LocalDateTime dataHoraEvento = LocalDateTime.of(
	        this.dataEvento.toLocalDate(), 
	        horaEvento.toLocalTime()
	    );
	    
	    LocalDateTime agora = LocalDateTime.now();
	    LocalDateTime umaHoraAFrente = agora.plusHours(1);
	    
	    if (dataHoraEvento.isBefore(umaHoraAFrente)) {
	        throw new IllegalArgumentException(
	            "Horário do evento deve ser pelo menos 1 hora a partir de agora."
	        );
	    }
	    this.horaEvento = horaEvento;
	}


	public Duration getDuracaoEvento() {
		return duracaoEvento;
	}

	public void setDuracaoEvento(Duration duracaoEvento) {
		this.duracaoEvento = duracaoEvento;
	}

	public HashMap<Integer, Administrador> getOrganizadores() {
		return organizadores;
	}

	public void setOrganizadores(HashMap<Integer, Administrador> organizadores) {
		this.organizadores = organizadores;
	}

	public HashMap<Integer, Participante> getParticipantes() {
		return participantes;
	}

	public void setParticipantes(HashMap<Integer, Participante> participantes) {
		this.participantes = participantes;
	}

	public String toString() {
	    return this.titulo; // Exibe apenas o título no JComboBox
	}
	
	public int getCapacidadeRestante() {
	    return capacidadeMaxima - participantes.size();
	}

	
	
	private int quantidadeInscricoes;

	public int getQuantidadeInscricoes() {
	    return quantidadeInscricoes;
	}

	public void setQuantidadeInscricoes(int quantidadeInscricoes) {
	    this.quantidadeInscricoes = quantidadeInscricoes;
	}
}
