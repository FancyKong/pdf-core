package com.cafa.pdf.core.service;

import com.cafa.pdf.core.commom.dto.AuthorDTO;
import com.cafa.pdf.core.commom.enums.ActiveEnum;
import com.cafa.pdf.core.commom.shiro.CryptographyUtil;
import com.cafa.pdf.core.dal.dao.IBaseDAO;
import com.cafa.pdf.core.dal.dao.AuthorDAO;
import com.cafa.pdf.core.dal.entity.Author;
import com.cafa.pdf.core.util.ObjectConvertUtil;
import com.cafa.pdf.core.web.request.BasicSearchReq;
import com.cafa.pdf.core.web.request.author.AuthorSaveReq;
import com.cafa.pdf.core.web.request.author.AuthorSearchReq;
import com.cafa.pdf.core.web.request.author.AuthorUpdateReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AuthorService extends ABaseService<Author, Long> {

    private final AuthorDAO authorDAO;

    @Autowired
    public AuthorService(AuthorDAO authorDAO) {
        this.authorDAO = authorDAO;
    }

    @Override
    protected IBaseDAO<Author, Long> getEntityDAO() {
        return authorDAO;
    }

    public Author findByTelephone(String telephone) {
        return authorDAO.findByTelephone(telephone);
    }

    public boolean exist(String telephone) {
        return authorDAO.findByTelephone(telephone) != null;
    }

    public Long getCount() {
        log.debug("countAllAuthor没有缓存");
        return authorDAO.count();
    }

    @Transactional(readOnly = false)
    public void delete(Long id) {
        // 并不是真正的删除，只是冻结账户
        Author author = findById(id);
        author.setActive(0);
        update(author);
    }

    @Transactional
    public void update(AuthorUpdateReq authorUpdateReq) {
        Author author = findById(authorUpdateReq.getId());
        ObjectConvertUtil.objectCopy(author, authorUpdateReq);
        author.setModifiedTime(new Date());
        update(author);
    }

    @Transactional
    public void save(AuthorSaveReq authorSaveReq) {

        if (exist(authorSaveReq.getTelephone())) {
            return;
        }

        Author author = new Author();
        ObjectConvertUtil.objectCopy(author, authorSaveReq);
        author.setCreatedTime(new Date());
        author.setModifiedTime(new Date());
        author.setPassword(CryptographyUtil.cherishSha1(author.getPassword()));
        save(author);
    }

    public Page<AuthorDTO> findAll(BasicSearchReq basicSearchReq, AuthorSearchReq authorSearchReq) {

        int pageNumber = basicSearchReq.getStartIndex() / basicSearchReq.getPageSize() + 1;
        PageRequest pageRequest = super.buildPageRequest(pageNumber, basicSearchReq.getPageSize());

        //除了分页条件没有特定搜索条件的，为了缓存count
        if (ObjectConvertUtil.objectFieldIsBlank(authorSearchReq)) {
            log.debug("没有特定搜索条件的");
            List<Author> authorList = authorDAO.listAllPaged(pageRequest);
            List<AuthorDTO> authorDTOList = authorList.stream().map(this::getAuthorDTO).collect(Collectors.toList());

            //为了计算总数使用缓存，加快搜索速度
            Long count = getCount();
            return new PageImpl<>(authorDTOList, pageRequest, count);
        }

        //有了其它搜索条件
        Page<Author> authorPage = super.findAllBySearchParams(
                buildSearchParams(authorSearchReq), pageNumber, basicSearchReq.getPageSize());

        return authorPage.map(this::getAuthorDTO);

    }

    /**
     * 转成DTO
     * @param source Author
     * @return AuthorDTO
     */
    private AuthorDTO getAuthorDTO(Author source) {
        AuthorDTO authorDTO = new AuthorDTO();
        ObjectConvertUtil.objectCopy(authorDTO, source);
        authorDTO.setActiveStr(ActiveEnum.getDesc(source.getActive()));
        return authorDTO;
    }

}
