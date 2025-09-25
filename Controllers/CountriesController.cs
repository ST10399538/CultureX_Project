using CultureXAPI.Data;
using CultureXAPI.DTOs;
using CultureXAPI.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Microsoft.AspNetCore.Authorization;
using Newtonsoft.Json;

namespace CultureXAPI.Controllers
{

    [ApiController]
    [Route("api/[controller]")]
    public class CountriesController : ControllerBase
    {

        private readonly CultureXDbContext _context;

        public CountriesController(CultureXDbContext context)
        {
            _context = context;
        }

        [HttpGet]
        public async Task<ActionResult<IEnumerable<CountryDTO>>> GetCountries()
        {
            var countries = await _context.Countries
                .Select(c => new CountryDTO
                {
                    Id = c.Id,
                    Name = c.Name,
                    CountryCode = c.CountryCode,
                    FlagImageUrl = c.FlagImageUrl,
                    Description = c.Description,
                    Timezone = c.Timezone,
                    Currency = c.Currency,
                    EmergencyContacts = JsonConvert.DeserializeObject(c.EmergencyContacts ?? "{}")
                })
                .ToListAsync();

            return Ok(countries);
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<CountryDTO>> GetCountry(Guid id)
        {
            var country = await _context.Countries.FindAsync(id);

            if (country == null)
            {
                return NotFound("Country not found");
            }

            var countryDto = new CountryDTO
            {
                Id = country.Id,
                Name = country.Name,
                CountryCode = country.CountryCode,
                FlagImageUrl = country.FlagImageUrl,
                Description = country.Description,
                Timezone = country.Timezone,
                Currency = country.Currency,
                EmergencyContacts = JsonConvert.DeserializeObject(country.EmergencyContacts ?? "{}")
            };

            return Ok(countryDto);
        }

        [HttpGet("{id}/categories")]
        public async Task<ActionResult<IEnumerable<CulturalCategoryDTO>>> GetCountryCategories(Guid id)
        {
            var country = await _context.Countries.FindAsync(id);
            if (country == null)
            {
                return NotFound("Country not found");
            }

            var categories = await _context.CulturalCategories
                .Where(cc => _context.CulturalContents.Any(content => content.CountryId == id && content.CategoryId == cc.Id))
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
        public async Task<ActionResult<IEnumerable<CountryDTO>>> SearchCountries([FromQuery] string query)
        {
            if (string.IsNullOrWhiteSpace(query))
            {
                return BadRequest("Search query is required");
            }

            var countries = await _context.Countries
                .Where(c => c.Name.Contains(query) || c.CountryCode.Contains(query))
                .Select(c => new CountryDTO
                {
                    Id = c.Id,
                    Name = c.Name,
                    CountryCode = c.CountryCode,
                    FlagImageUrl = c.FlagImageUrl,
                    Description = c.Description,
                    Timezone = c.Timezone,
                    Currency = c.Currency,
                    EmergencyContacts = JsonConvert.DeserializeObject(c.EmergencyContacts ?? "{}")
                })
                .ToListAsync();

            return Ok(countries);
        }

    }
}
