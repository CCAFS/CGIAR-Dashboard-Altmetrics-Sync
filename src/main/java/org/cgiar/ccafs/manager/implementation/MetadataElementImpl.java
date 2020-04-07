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

import org.cgiar.ccafs.dao.MetadataElementDAO;
import org.cgiar.ccafs.domain.marlo.MetadataElement;
import org.cgiar.ccafs.manager.MetadataElementManager;

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
public class MetadataElementImpl implements MetadataElementManager {

  @Autowired
  MetadataElementDAO metadataElementDAO;

  @Autowired
  Validator validator;

  @Override
  @Transactional(readOnly = true)
  public Long count() {
    return metadataElementDAO.count();
  }

  @Override
  @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public void delete(MetadataElement entity) throws Exception {
    String id = entity.getId();
    if (StringUtils.isBlank(id)) {
      throw new Exception("Detached entity");
    }

    if (metadataElementDAO.existsById(id) == false) {
      throw new Exception("Entity does not exist in the database");
    }

    entity = metadataElementDAO.findById(id).get();

    // metadataElementDAO.delete(entity);
  }

  @Override
  @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public void deleteById(String id) throws Exception {
    if (StringUtils.isBlank(id)) {
      throw new Exception("ID can not be null");
    }

    if (metadataElementDAO.existsById(id) == false) {
      throw new Exception("Entity does not exist in the database");
    }

    // delete(findById(id).get());
  }

  @Override
  @Transactional(readOnly = true)
  public List<MetadataElement> findAll() {
    return metadataElementDAO.findAll();
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<MetadataElement> findById(String id) {
    id = StringUtils.stripToEmpty(id);
    return metadataElementDAO.findById(id);
  }

  @Override
  @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public MetadataElement save(MetadataElement entity) throws Exception {
    this.validate(entity);
    metadataElementDAO.save(entity);
    return entity;
  }

  @Override
  @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public MetadataElement update(MetadataElement entity) throws Exception {
    this.validate(entity);

    String id = entity.getId();
    if (StringUtils.isBlank(id)) {
      throw new Exception("Detached entity");
    }

    if (metadataElementDAO.existsById(id) == false) {
      throw new Exception("Entity does not exist in the database");
    }

    metadataElementDAO.save(entity);
    return entity;
  }

  @Override
  public void validate(MetadataElement entity) throws Exception {
    Objects.requireNonNull(entity);

    Set<ConstraintViolation<MetadataElement>> constraintViolations = validator.validate(entity);

    if (!constraintViolations.isEmpty()) {
      StringBuilder strMessage = new StringBuilder();

      for (ConstraintViolation<MetadataElement> constraintViolation : constraintViolations) {
        strMessage.append(constraintViolation.getPropertyPath().toString());
        strMessage.append(" - ");
        strMessage.append(constraintViolation.getMessage());
        strMessage.append(". \n");
      }

      throw new Exception(strMessage.toString());
    }
  }

}
