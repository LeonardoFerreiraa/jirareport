package br.com.leonardoferreira.jirareport.domain;

import java.io.Serializable;

import br.com.leonardoferreira.jirareport.util.DateUtil;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author s2it_leferreira
 * @since 5/7/18 6:49 PM
 */
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class Holiday extends BaseEntity {
    private static final long serialVersionUID = 18640912961216513L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "A data deve ser informada.")
    @Pattern(regexp = "[0-9]{2}/[0-9]{2}/[0-9]{4}", message = "Deve ser uma data valida.")
    private String date;

    @NotEmpty(message = "A descrição deve ser informada.")
    private String description;

    @Transient
    public String getEnDate() {
        return DateUtil.toENDate(date);
    }

}