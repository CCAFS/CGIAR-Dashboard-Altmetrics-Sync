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

import org.cgiar.ccafs.dao.PhaseDAO;
import org.cgiar.ccafs.domain.marlo.Phase;
import org.cgiar.ccafs.manager.PhaseManager;

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
public class PhaseManagerImpl implements PhaseManager {

  @Autowired
  PhaseDAO phaseDAO;

  @Autowired
  Validator validator;

  @Override
  @Transactional(readOnly = true)
  public Long count() {
    return phaseDAO.count();
  }

  @Override
  @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public void delete(Phase entity) throws Exception {
    String id = entity.getId();
    if (StringUtils.isBlank(id)) {
      throw new Exception("Detached entity");
    }

    if (phaseDAO.existsById(id) == false) {
      throw new Exception("Entity does not exist in the database");
    }

    entity = phaseDAO.findById(id).get();

    // phaseDAO.delete(entity);
  }

  @Override
  @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public void deleteById(String id) throws Exception {
    if (StringUtils.isBlank(id)) {
      throw new Exception("ID can not be null");
    }

    if (phaseDAO.existsById(id) == false) {
      throw new Exception("Entity does not exist in the database");
    }

    // delete(findById(id).get());
  }

  @Override
  @Transactional(readOnly = true)
  public List<Phase> findAll() {
    return phaseDAO.findAll();
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Phase> findById(String id) {
    id = StringUtils.stripToEmpty(id);
    return phaseDAO.findById(id);
  }

  @Override
  @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public Phase save(Phase entity) throws Exception {
    this.validate(entity);
    phaseDAO.save(entity);
    return entity;
  }

  @Override
  @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public Phase update(Phase entity) throws Exception {
    this.validate(entity);

    String id = entity.getId();
    if (StringUtils.isBlank(id)) {
      throw new Exception("Detached entity");
    }

    if (phaseDAO.existsById(id) == false) {
      throw new Exception("Entity does not exist in the database");
    }

    phaseDAO.save(entity);
    return entity;
  }

  @Override
  public void validate(Phase entity) throws Exception {
    Objects.requireNonNull(entity);

    Set<ConstraintViolation<Phase>> constraintViolations = validator.validate(entity);

    if (!constraintViolations.isEmpty()) {
      StringBuilder strMessage = new StringBuilder();

      for (ConstraintViolation<Phase> constraintViolation : constraintViolations) {
        strMessage.append(constraintViolation.getPropertyPath().toString());
        strMessage.append(" - ");
        strMessage.append(constraintViolation.getMessage());
        strMessage.append(". \n");
      }

      throw new Exception(strMessage.toString());
    }
  }

}
