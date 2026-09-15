import { Link } from 'react-router-dom'

// The car cards shown on the leaderboard landing page. Each links to the
// details page with the same name/internalName query params as the original.
const cars = [
  {
    image: '/images/SF24.png',
    name: 'SF23-challenge6_TopSpeed',
    internalName: 'SF23  - Desafío 6: Máxima velocidad',
    label: 'SF23  - Desafío 6: Máxima velocidad',
  },
  {
    image: '/images/DaytonaSP3.png',
    name: 'DAYTONASP3-challenge6_TopSpeed',
    internalName: 'DAYTONA SP3  - Desafío 6: Máxima velocidad',
    label: 'DAYTONA SP3  - Desafío 6: Máxima velocidad',
  },
  {
    image: '/images/296GTB.png',
    name: '296GTB-challenge6_TopSpeed',
    internalName: '296GTB  - Desafío 6: Máxima velocidad',
    label: '296GTB  - Desafío 6: Máxima velocidad',
  },
  {
    image: '/images/330P4.png',
    name: '330P4-challenge6_TopSpeed',
    internalName: '330P4  - Desafío 6: Máxima velocidad',
    label: '330P4  - Desafío 6: Máxima velocidad',
  },
]

export default function Leaderboard() {
  return (
    <>
      <div className="row">
        <div
          className="col-xs-12 d-flex justify-content-center"
          style={{
            alignContent: 'center',
            alignItems: 'center',
            width: '100%',
            borderBottom: '35px',
          }}
        >
          <img style={{ maxHeight: '75px' }} src="/images/srl-logo.png" alt="SRL logo" />
        </div>
      </div>
      <div className="row">
        <div className="col-md-12">
          <h1>
            Tabla de Clasificación – Por coche (Pincha en cada modelo para ver el
            detalle)
          </h1>
        </div>
      </div>
      <div className="col-lg-12">
        <div className="row">
          {cars.map((car) => (
            <div key={car.name} className="col-lg-3 col-md-12 col-xs-12">
              <Link
                to={`/leader-board-details?name=${encodeURIComponent(
                  car.name,
                )}&internalName=${encodeURIComponent(car.internalName)}`}
                style={{ textDecoration: 'none' }}
              >
                <img style={{ maxWidth: '80%' }} src={car.image} alt={car.label} />
                <br />
                <span>{car.label}</span>
              </Link>
            </div>
          ))}
        </div>
      </div>
    </>
  )
}
