/*****************************************************************
 * This file is part of Managing Agricultural Research for Learning &
 * Outcomes Platform (MARLO).
 * MARLO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * MARLO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with MARLO. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/

package org.cgiar.ccafs.manager.implementation;

import org.cgiar.ccafs.dao.ReportSynthesisAltmetricDAO;
import org.cgiar.ccafs.domain.marlo.ReportSynthesisAltmetric;
import org.cgiar.ccafs.manager.ReportSynthesisAltmetricManager;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

@Service
@Scope("singleton")
public class ReportSynthesisAltmetricManagerImpl implements ReportSynthesisAltmetricManager {

  @Autowired
  ReportSynthesisAltmetricDAO reportSynthesisAltmetricDAO;

  @Autowired
  Validator validator;

  @Override
  @Transactional(readOnly = true)
  public Long count() {
    return reportSynthesisAltmetricDAO.count();
  }

  @Override
  @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public void delete(ReportSynthesisAltmetric entity) throws Exception {
    String id = entity.getId();
    if (StringUtils.isBlank(id)) {
      throw new Exception("Detached entity");
    }

    if (reportSynthesisAltmetricDAO.existsById(id) == false) {
      throw new Exception("Entity does not exist in the database");
    }

    entity = reportSynthesisAltmetricDAO.findById(id).get();

    // reportSynthesisAltmetricDAO.delete(entity);
  }

  @Override
  @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public void deleteById(String id) throws Exception {
    if (StringUtils.isBlank(id)) {
      throw new Exception("ID can not be null");
    }

    if (reportSynthesisAltmetricDAO.existsById(id) == false) {
      throw new Exception("Entity does not exist in the database");
    }

    // delete(findById(id).get());
  }

  @Override
  @Transactional(readOnly = true)
  public List<ReportSynthesisAltmetric> findAll() {
    return reportSynthesisAltmetricDAO.findAll();
  }

  @Override
  public List<ReportSynthesisAltmetric> findByCRPAcronym(String crpAcronym) {
    crpAcronym = StringUtils.stripToEmpty(crpAcronym);
    if (crpAcronym.isEmpty()) {
      return Collections.emptyList();
    }

    return reportSynthesisAltmetricDAO.findByCRPAcronym(crpAcronym);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<ReportSynthesisAltmetric> findById(String id) {
    id = StringUtils.stripToEmpty(id);
    return reportSynthesisAltmetricDAO.findById(id);
  }

  @Override
  @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public ReportSynthesisAltmetric save(ReportSynthesisAltmetric entity) throws Exception {
    this.validate(entity);
    reportSynthesisAltmetricDAO.save(entity);
    return entity;
  }

  @Override
  @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public ReportSynthesisAltmetric update(ReportSynthesisAltmetric entity) throws Exception {
    this.validate(entity);

    String id = entity.getId();
    if (StringUtils.isBlank(id)) {
      throw new Exception("Detached entity");
    }

    if (reportSynthesisAltmetricDAO.existsById(id) == false) {
      throw new Exception("Entity does not exist in the database");
    }

    reportSynthesisAltmetricDAO.save(entity);
    return entity;
  }

  @Override
  public void validate(ReportSynthesisAltmetric entity) throws Exception {
    Objects.requireNonNull(entity);

    Set<ConstraintViolation<ReportSynthesisAltmetric>> constraintViolations = validator.validate(entity);

    if (!constraintViolations.isEmpty()) {
      StringBuilder strMessage = new StringBuilder();

      for (ConstraintViolation<ReportSynthesisAltmetric> constraintViolation : constraintViolations) {
        strMessage.append(constraintViolation.getPropertyPath().toString());
        strMessage.append(" - ");
        strMessage.append(constraintViolation.getMessage());
        strMessage.append(". \n");
      }

      throw new Exception(strMessage.toString());
    }
  }

}
