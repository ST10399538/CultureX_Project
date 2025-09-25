using CultureXAPI.Data;
using CultureXAPI.Models;
using CultureXAPI.DTOs;
using Microsoft.AspNetCore.Authorization;
using Microsoft.EntityFrameworkCore;
using Microsoft.AspNetCore.Mvc;
using Newtonsoft.Json;
using System.Security.Claims;

namespace CultureXAPI.Controllers
{

    [ApiController]
    [Route("api/[controller]")]
    [Authorize]
    public class ItinerariesController : ControllerBase
    {

        private readonly CultureXDbContext _context;

        public ItinerariesController(CultureXDbContext context)
        {
            _context = context;
        }

        private Guid GetUserId()
        {
            var userIdClaim = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
            return Guid.Parse(userIdClaim);
        }

        [HttpGet]
        public async Task<ActionResult<IEnumerable<ItineraryDTO>>> GetUserItineraries()
        {
            var userId = GetUserId();

            var itineraries = await _context.UserItineraries
                .Include(ui => ui.Country)
                .Where(ui => ui.UserId == userId)
                .Select(ui => new ItineraryDTO
                {
                    Id = ui.Id,
                    UserId = ui.UserId,
                    CountryId = ui.CountryId,
                    Title = ui.Title,
                    Description = ui.Description,
                    StartDate = ui.StartDate,
                    EndDate = ui.EndDate,
                    Activities = JsonConvert.DeserializeObject<string[]>(ui.Activities ?? "[]"),
                    CountryName = ui.Country.Name,
                    CreatedAt = ui.CreatedAt,
                    UpdatedAt = ui.UpdatedAt
                })
                .OrderByDescending(ui => ui.CreatedAt)
                .ToListAsync();

            return Ok(itineraries);
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<ItineraryDTO>> GetItinerary(Guid id)
        {
            var userId = GetUserId();

            var itinerary = await _context.UserItineraries
                .Include(ui => ui.Country)
                .FirstOrDefaultAsync(ui => ui.Id == id && ui.UserId == userId);

            if (itinerary == null)
            {
                return NotFound("Itinerary not found");
            }

            var itineraryDto = new ItineraryDTO
            {
                Id = itinerary.Id,
                UserId = itinerary.UserId,
                CountryId = itinerary.CountryId,
                Title = itinerary.Title,
                Description = itinerary.Description,
                StartDate = itinerary.StartDate,
                EndDate = itinerary.EndDate,
                Activities = JsonConvert.DeserializeObject<string[]>(itinerary.Activities ?? "[]"),
                CountryName = itinerary.Country.Name,
                CreatedAt = itinerary.CreatedAt,
                UpdatedAt = itinerary.UpdatedAt
            };

            return Ok(itineraryDto);
        }

        [HttpPost]
        public async Task<ActionResult<ItineraryDTO>> CreateItinerary(CreateItineraryDTO createItineraryDto)
        {
            var userId = GetUserId();

            // Verify country exists
            var country = await _context.Countries.FindAsync(createItineraryDto.CountryId);
            if (country == null)
            {
                return BadRequest("Country not found");
            }

            var itinerary = new UserItinerary
            {
                UserId = userId,
                CountryId = createItineraryDto.CountryId,
                Title = createItineraryDto.Title,
                Description = createItineraryDto.Description,
                StartDate = createItineraryDto.StartDate,
                EndDate = createItineraryDto.EndDate,
                Activities = JsonConvert.SerializeObject(createItineraryDto.Activities ?? new string[0])
            };

            _context.UserItineraries.Add(itinerary);
            await _context.SaveChangesAsync();

            // Load the country for response
            await _context.Entry(itinerary).Reference(i => i.Country).LoadAsync();

            var itineraryDto = new ItineraryDTO
            {
                Id = itinerary.Id,
                UserId = itinerary.UserId,
                CountryId = itinerary.CountryId,
                Title = itinerary.Title,
                Description = itinerary.Description,
                StartDate = itinerary.StartDate,
                EndDate = itinerary.EndDate,
                Activities = JsonConvert.DeserializeObject<string[]>(itinerary.Activities ?? "[]"),
                CountryName = itinerary.Country.Name,
                CreatedAt = itinerary.CreatedAt,
                UpdatedAt = itinerary.UpdatedAt
            };

            return CreatedAtAction(nameof(GetItinerary), new { id = itinerary.Id }, itineraryDto);
        }

        [HttpPut("{id}")]
        public async Task<ActionResult<ItineraryDTO>> UpdateItinerary(Guid id, UpdateItineraryDTO updateItineraryDto)
        {
            var userId = GetUserId();

            var itinerary = await _context.UserItineraries
                .Include(ui => ui.Country)
                .FirstOrDefaultAsync(ui => ui.Id == id && ui.UserId == userId);

            if (itinerary == null)
            {
                return NotFound("Itinerary not found");
            }

            itinerary.Title = updateItineraryDto.Title;
            itinerary.Description = updateItineraryDto.Description;
            itinerary.StartDate = updateItineraryDto.StartDate;
            itinerary.EndDate = updateItineraryDto.EndDate;
            itinerary.Activities = JsonConvert.SerializeObject(updateItineraryDto.Activities ?? new string[0]);
            itinerary.UpdatedAt = DateTime.UtcNow;

            await _context.SaveChangesAsync();

            var itineraryDto = new ItineraryDTO
            {
                Id = itinerary.Id,
                UserId = itinerary.UserId,
                CountryId = itinerary.CountryId,
                Title = itinerary.Title,
                Description = itinerary.Description,
                StartDate = itinerary.StartDate,
                EndDate = itinerary.EndDate,
                Activities = JsonConvert.DeserializeObject<string[]>(itinerary.Activities ?? "[]"),
                CountryName = itinerary.Country.Name,
                CreatedAt = itinerary.CreatedAt,
                UpdatedAt = itinerary.UpdatedAt
            };

            return Ok(itineraryDto);
        }

        [HttpDelete("{id}")]
        public async Task<ActionResult> DeleteItinerary(Guid id)
        {
            var userId = GetUserId();

            var itinerary = await _context.UserItineraries
                .FirstOrDefaultAsync(ui => ui.Id == id && ui.UserId == userId);

            if (itinerary == null)
            {
                return NotFound("Itinerary not found");
            }

            _context.UserItineraries.Remove(itinerary);
            await _context.SaveChangesAsync();

            return NoContent();
        }
    }

}
