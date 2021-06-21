package com.rastreamento.correios.model;

import com.rastreamento.correios.utils.StringUtils;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Gustavo Bussolotti
 */
@Entity
@Table(name = "item", indexes = {
        @Index(name = "unique_cod_rastreio", columnList = "codigo_rastreio", unique = true)})
@XmlRootElement
public class CadItem extends Cadastro implements Serializable, Comparable<CadItem> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;

    @Basic(optional = false)
    @Column(name = "descricao")
    private String description;

    @Column(name = "descricao_atualizacao")
    private String descriptioUpdate;

    @Basic(optional = false)
    @Column(name = "codigo_rastreio")
    private String trackCode;

    @Basic(optional = false)
    @Column(name = "dt_insercao", columnDefinition = "TIMESTAMP")
    private LocalDateTime dtInsert;

    @Column(name = "dt_atualizacao",columnDefinition = "TIMESTAMP")
    private LocalDateTime dtUpdate;

    @Column(name = "dt_postagem", columnDefinition = "DATE")
    private LocalDate dtPost;

    @Basic(optional = false)
    @Column(name = "consultar", columnDefinition = "boolean default true")
    private Boolean toConsult;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "item")
    private List<CadItemStatus> itemStatusList;

    public CadItem() {
    }

    public CadItem(Integer id) {
        this.id = id;
    }

    public CadItem(Integer id, String description, String trackCode) {
        this.id = id;
        this.description = description;
        this.trackCode = trackCode;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = StringUtils.limitadorString(description, 50);
    }

    public String getTrackCode() {
        return trackCode.toUpperCase();
    }

    public void setTrackCode(String trackCode) {
        this.trackCode = StringUtils.limitadorString(trackCode, 13);
    }

    @XmlTransient
    public List<CadItemStatus> getItemStatusList() {
        return itemStatusList;
    }

    public void setItemStatusList(List<CadItemStatus> itemStatusList) {
        this.itemStatusList = itemStatusList;
    }

    public LocalDate getDtPost() {
        return dtPost;
    }

    public void setDtPost(LocalDate dtPost) {
        this.dtPost = dtPost;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CadItem)) {
            return false;
        }
        CadItem other = (CadItem) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    public String getDescriptioUpdate() {
        return descriptioUpdate;
    }

    public void setDescriptioUpdate(String descriptioUpdate) {
        this.descriptioUpdate = descriptioUpdate;
    }

    @Override
    public String toString() {
        return "[ id=" + id + ", cod="+ trackCode +" ]";
    }

    public LocalDateTime getDtUpdate() {
        return dtUpdate;
    }

    public void setDtUpdate(LocalDateTime dtUpdate) {
        this.dtUpdate = dtUpdate;
    }

    public LocalDateTime getDtInsert() {
        return dtInsert;
    }

    public void setDtInsert(LocalDateTime dtInsert) {
        this.dtInsert = dtInsert;
    }

    public Boolean getToConsult() {
        return toConsult == null ? Boolean.TRUE : toConsult;
    }

    public void setToConsult(Boolean toConsult) {
        this.toConsult = toConsult;
    }

    @Override
    public int compareTo(CadItem other) {
        if (this.getDtUpdate() == null) {
            return -1;
        }
        if (other.getDtUpdate() == null) {
            return 1;
        }

        if (this.getDtUpdate().isBefore(other.getDtUpdate())) {
            return 1;
        } else if (this.getDtUpdate().isAfter(other.getDtUpdate())) {
            return -1;
        } else {
            return 0;
        }
    }

}

