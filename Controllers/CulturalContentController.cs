using CultureXAPI.Data;
using CultureXAPI.DTOs;
using CultureXAPI.Models;
using Microsoft.EntityFrameworkCore;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Newtonsoft.Json;

namespace CultureXAPI.Controllers
{

    [ApiController]
    [Route("api/[controller]")]
    public class CulturalContentController : ControllerBase
    {

        private readonly CultureXDbContext _context;

        public CulturalContentController(CultureXDbContext context)
        {
            _context = context;
        }

        [HttpGet("countries/{countryId}/content/{categoryId}")]
        public async Task<ActionResult<CulturalContentDTO>> GetCulturalContent(Guid countryId, Guid categoryId)
        {
            var content = await _context.CulturalContents
                .Include(cc => cc.Country)
                .Include(cc => cc.Category)
                .FirstOrDefaultAsync(cc => cc.CountryId == countryId && cc.CategoryId == categoryId);

            if (content == null)
            {
                return NotFound("Cultural content not found");
            }

            var contentDto = new CulturalContentDTO
            {
                Id = content.Id,
                CountryId = content.CountryId,
                CategoryId = content.CategoryId,
                Title = content.Title,
                Content = content.Content,
                Dos = JsonConvert.DeserializeObject<string[]>(content.Dos ?? "[]"),
                Donts = JsonConvert.DeserializeObject<string[]>(content.Donts ?? "[]"),
                Examples = JsonConvert.DeserializeObject<string[]>(content.Examples ?? "[]"),
                CountryName = content.Country.Name,
                CategoryName = content.Category.Name
            };

            return Ok(contentDto);
        }

        [HttpGet("countries/{countryId}")]
        public async Task<ActionResult<IEnumerable<CulturalContentDTO>>> GetCountryCulturalContent(Guid countryId)
        {
            var country = await _context.Countries.FindAsync(countryId);
            if (country == null)
            {
                return NotFound("Country not found");
            }

            var contents = await _context.CulturalContents
                .Include(cc => cc.Country)
                .Include(cc => cc.Category)
                .Where(cc => cc.CountryId == countryId)
                .Select(cc => new CulturalContentDTO
                {
                    Id = cc.Id,
                    CountryId = cc.CountryId,
                    CategoryId = cc.CategoryId,
                    Title = cc.Title,
                    Content = cc.Content,
                    Dos = JsonConvert.DeserializeObject<string[]>(cc.Dos ?? "[]"),
                    Donts = JsonConvert.DeserializeObject<string[]>(cc.Donts ?? "[]"),
                    Examples = JsonConvert.DeserializeObject<string[]>(cc.Examples ?? "[]"),
                    CountryName = cc.Country.Name,
                    CategoryName = cc.Category.Name
                })
                .ToListAsync();

            return Ok(contents);
        }

        [HttpGet("categories")]
        public async Task<ActionResult<IEnumerable<CulturalCategoryDTO>>> GetCategories()
        {
            var categories = await _context.CulturalCategories
                .Select(cc => new CulturalCategoryDTO
                {
                    Id = cc.Id,
                    Name = cc.Name,
                    Description = cc.Description,
                    IconUrl = cc.IconUrl,
                    SortOrder = cc.SortOrder
                })
                .OrderBy(cc => cc.SortOrder)
                .ToListAsync();

            return Ok(categories);
        }

        [HttpGet("search")]
        public async Task<ActionResult<IEnumerable<CulturalContentDTO>>> SearchContent([FromQuery] string query)
        {
            if (string.IsNullOrWhiteSpace(query))
            {
                return BadRequest("Search query is required");
            }

            var contents = await _context.CulturalContents
                .Include(cc => cc.Country)
                .Include(cc => cc.Category)
                .Where(cc => cc.Title.Contains(query) || cc.Content.Contains(query))
                .Select(cc => new CulturalContentDTO
                {
                    Id = cc.Id,
                    CountryId = cc.CountryId,
                    CategoryId = cc.CategoryId,
                    Title = cc.Title,
                    Content = cc.Content,
                    Dos = JsonConvert.DeserializeObject<string[]>(cc.Dos ?? "[]"),
                    Donts = JsonConvert.DeserializeObject<string[]>(cc.Donts ?? "[]"),
                    Examples = JsonConvert.DeserializeObject<string[]>(cc.Examples ?? "[]"),
                    CountryName = cc.Country.Name,
                    CategoryName = cc.Category.Name
                })
                .ToListAsync();

            return Ok(contents);
        }
    }

}

